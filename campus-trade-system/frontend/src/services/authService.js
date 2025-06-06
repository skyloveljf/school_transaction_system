// src/services/authService.js
import axios from 'axios';
import { useUserStore } from '@/store/user';

const API_BASE_URL = '/api';

// 存储键名
const TOKEN_KEY = 'token';
const USER_KEY = 'userProfileDto';

// 认证服务
const authService = {
  // 获取token
  getToken() {
    const token = localStorage.getItem(TOKEN_KEY);
    if (token) {
      console.log('Found token in localStorage:', token.substring(0, 15) + '...');
    } else {
      console.log('No token found in localStorage');
    }
    return token;
  },

  // 获取用户角色
  getUserRole() {
    const userStore = useUserStore();
    return userStore.role || '';
  },

  // 判断是否是管理员
  isAdmin() {
    return this.getUserRole() === 'ROLE_ADMIN';
  },

  // 获取认证头部（为API请求提供Authorization头）
  getAuthHeader() {
    const token = this.getToken();
    if (token) {
      return { 'Authorization': `Bearer ${token}` };
    }
    return {};
  },

  // 获取用户信息
  getUserProfile() {
    const userJson = localStorage.getItem(USER_KEY);
    if (!userJson) {
      console.log('No user profile found in localStorage');
      return null;
    }
    
    try {
      const userProfile = JSON.parse(userJson);
      console.log('Found user profile in localStorage:', userProfile.username || userProfile.userId);
      return userProfile;
    } catch (e) {
      console.error('Error parsing user profile from localStorage', e);
      return null;
    }
  },

  // 保存登录信息
  saveLoginInfo(accessToken, userProfileDto) {
    console.log('Saving login info to localStorage and userStore');
    const userStore = useUserStore();

    if (accessToken) {
      console.log('Saving token:', accessToken.substring(0, 15) + '...');
      localStorage.setItem(TOKEN_KEY, accessToken);
    } else {
      console.warn('No token provided to saveLoginInfo');
    }
    
    if (userProfileDto) {
      console.log('Saving user profile DTO to localStorage:', userProfileDto.username || userProfileDto.userId);
      localStorage.setItem(USER_KEY, JSON.stringify(userProfileDto));

      userStore.setUserInfo({
        token: accessToken,
        userId: userProfileDto.userId,
        username: userProfileDto.username,
        avatar: userProfileDto.avatarUrl,
        role: userProfileDto.role
      });
      console.log('User info saved to userStore:', { 
        userId: userStore.userId, username: userStore.username, avatar: userStore.avatar, role: userStore.role 
      });

    } else {
      console.warn('No user profile DTO provided to saveLoginInfo, only saving token to store if available.');
      userStore.setUserInfo({ 
        token: accessToken, 
        userId: null, 
        username: '', 
        avatar: '', 
        role: '' 
      });
    }
  },

  // 检查用户是否已登录
  isLoggedIn() {
    const hasToken = !!this.getToken();
    console.log('isLoggedIn check:', hasToken);
    return hasToken;
  },

  // 重定向到相应页面
  redirectToUserHomePage() {
    const userStore = useUserStore();
    if (userStore.isAdmin) {
      // 如果是管理员，跳转到管理员页面
      window.location.href = '/admin';
    } else {
      // 如果是普通用户，跳转到用户首页
      window.location.href = '/userhome';
    }
  },

  // 清除登录信息
  clearLoginInfo() {
    console.log('Clearing login info from localStorage and userStore');
    const userStore = useUserStore();
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    userStore.clearUserInfo();
  },

  // 刷新token (可以根据需要实现)
  async refreshToken() {
    const refreshToken = localStorage.getItem('refreshToken'); // 如果有refresh token机制
    if (!refreshToken) {
      return Promise.reject(new Error('No refresh token available'));
    }
    
    try {
      const response = await axios.post(`${API_BASE_URL}/auth/refresh`, { refreshToken });
      if (response.data && response.data.accessToken) {
        localStorage.setItem(TOKEN_KEY, response.data.accessToken);
        return response.data.accessToken;
      }
    } catch (error) {
      console.error('Failed to refresh token:', error);
      this.clearLoginInfo(); // 如果刷新失败，清除登录信息
      throw error;
    }
  },

  // 设置Axios拦截器
  setupAxiosInterceptors() {
    console.log('Setting up axios interceptors');
    
    // 移除所有已存在的拦截器 (可能的原因，注释掉这些行以避免多次调用时出错)
    // axios.interceptors.request.eject(axios.interceptors.request.handlers[0]);
    // axios.interceptors.response.eject(axios.interceptors.response.handlers[0]);
    
    // 请求拦截器
    axios.interceptors.request.use(
      (config) => {
        console.log(`Request to ${config.url}`);
        const token = this.getToken();
        if (token) {
          console.log(`Found token in localStorage: ${token.substring(0, 15)}...`);
          console.log(`Adding token to request headers for ${config.url}`);
          // 确保Authorization头格式正确
          config.headers = {
            ...config.headers,
            'Authorization': `Bearer ${token}`
          };
        } else {
          console.warn(`No token available for request to ${config.url}`);
        }
        return config;
      },
      (error) => {
        console.error('Request interceptor error:', error);
        return Promise.reject(error);
      }
    );

    // 响应拦截器
    axios.interceptors.response.use(
      (response) => {
        console.log(`Response from ${response.config.url}: ${response.status}`);
        return response;
      },
      async (error) => {
        // 检查是否有错误响应
        if (error.response) {
          console.error(`Response error from ${error.config?.url}: ${error.response.status}`);
          
          // 处理401错误
          if (error.response.status === 401) {
            const originalRequest = error.config;
            
            // 防止无限重试
            if (!originalRequest._retry) {
              originalRequest._retry = true;
              
              // 检查是否有用户信息
              const userProfile = this.getUserProfile();
              if (userProfile) {
                console.warn('User info found but request failed with 401. Attempting to recover session...');
                
                // 清除登录信息
                this.clearLoginInfo();
                
                // 重定向到登录页面
                if (window.location.pathname !== '/') {
                  console.log('Redirecting to login page...');
                  // 使用history API或Vue Router进行重定向
                  window.location.href = '/';
                }
              }
            }
          }
        } else {
          console.error('Network error or no response:', error.message);
        }
        
        return Promise.reject(error);
      }
    );
    
    console.log('Axios interceptors setup complete');
  }
};

// 立即初始化拦截器
authService.setupAxiosInterceptors();

export default authService;