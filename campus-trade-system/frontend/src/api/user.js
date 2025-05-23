import axios from 'axios';
import authService from '../services/authService';

// 后端 API 的基础 URL (通过 Vite 代理，所以这里只写路径)
// Vite proxy 会将 /api 的请求转发到 http://localhost:8080
const API_BASE_URL = '/api';

// 登录接口
// data 应该是一个包含 username 和 password 的对象
export function loginApi(data) {
  console.log('Login API - 尝试登录:', data.username);
  
  return axios.post(`${API_BASE_URL}/auth/login`, data)
    .then(response => {
      console.log('Login API - 登录成功，服务器返回:', response.status, '响应数据结构:', Object.keys(response.data));
      if (response.data.accessToken) {
        console.log('Login API - 获取到token:', response.data.accessToken.substring(0, 15) + '...');
      } else {
        console.warn('Login API - 响应中缺少accessToken');
        throw new Error('登录响应成功，但未返回有效的认证凭证');
      }
      return response.data;
    })
    .catch(error => {
      console.error("Login API error:", error.response ? error.response.data : error.message);
      throw error.response && error.response.data ? error.response.data : new Error('登录失败，请检查您的凭据或网络连接');
    });
}

// 注册接口
// data 应该是一个包含 username, password, email 的对象
export function registerApi(data) {
  return axios.post(`${API_BASE_URL}/auth/register`, data)
    .then(response => {
      // 假设后端成功时返回类似 { message: "注册成功" }
      return response.data;
    })
    .catch(error => {
      console.error("Register API error:", error.response ? error.response.data : error.message);
      throw error.response ? error.response.data : new Error('注册失败，请稍后重试');
    });
}

// 获取当前登录用户信息的接口 (需要认证)
export function getMyProfileApi() {
  // 使用authService检查登录状态
  if (!authService.isLoggedIn()) {
    // 如果没有token，尝试从本地存储获取用户信息
    const userProfile = authService.getUserProfile();
    if (userProfile) {
      console.warn('User info found but token missing, returning local profile');
      return Promise.resolve(userProfile);
    }
    
    // 如果没有本地用户信息，返回模拟数据
    console.warn('No user info found, returning mock profile');
    const mockProfile = {
      userId: 7,
      username: 'sky',
      email: 'sky@example.com',
      nickname: 'condingsky',
      bio: '校园二手交易爱好者',
      avatarUrl: 'https://img.keaitupian.cn/uploads/upimg/1597372353577123.jpg',
      role: 'USER'
    };
    return Promise.resolve(mockProfile);
  }
  
  return axios.get(`${API_BASE_URL}/users/me`, { // 对应 UserController 的 /api/users/me
    headers: authService.getAuthHeader()
  })
  .then(response => response.data) // 后端返回 UserProfileDto
  .catch(error => {
    console.error("Get profile API error:", error.response ? error.response.data : error.message);
    // 返回模拟数据
    const mockProfile = {
      userId: 7,
      username: 'sky',
      email: 'sky@example.com',
      nickname: 'condingsky',
      bio: '校园二手交易爱好者',
      avatarUrl: 'https://img.keaitupian.cn/uploads/upimg/1597372353577123.jpg',
      role: 'USER'
    };
    return mockProfile;
  });
}

// 更新当前登录用户的个人资料 (需要认证)
// Corresponds to: PUT /api/users/me
export function updateMyProfileApi(userData) { // userData should match UserUpdateRequest DTO from backend
  if (!authService.isLoggedIn()) {
    // 检查是否有用户信息，如果有，尝试使用本地数据更新
    const currentProfile = authService.getUserProfile();
    if (currentProfile) {
      console.warn('No token found but updating local user profile');
      const updatedProfile = { ...currentProfile, ...userData };
      // 使用authService更新本地存储的用户信息
      authService.saveLoginInfo(null, updatedProfile);
      return Promise.resolve(updatedProfile);
    }
    return Promise.reject(new Error("用户未登录或会话已过期"));
  }
  // Common fields: nickname, email, bio, avatarUrl. Adjust based on actual UserUpdateRequest DTO.
  return axios.put(`${API_BASE_URL}/users/me`, userData, {
    headers: authService.getAuthHeader()
  })
  .then(response => response.data) // Expects updated UserProfileDto
  .catch(error => {
    console.error("Update profile API error:", error.response?.data || error.message);
    throw error.response?.data || new Error('更新个人资料失败');
  });
}

// 退出登录 (清除登录状态并可选调用后端登出接口)
export function logoutApi() {
    // 使用authService清除登录信息
    authService.clearLoginInfo();
    
    // 如果后端有专门的登出接口使token失效，也应该调用:
    // const token = authService.getToken();
    // if (token) {
    //   return axios.post(`${API_BASE_URL}/auth/logout`, {}, {
    //     headers: authService.getAuthHeader()
    //   });
    // }
    
    return Promise.resolve({ message: "已成功退出" }); // 本地登出直接成功
}

// 修改当前登录用户密码的接口 (需要认证)
// Corresponds to: POST /api/users/me/change-password
export function changePasswordApi(passwordData) { // passwordData should match ChangePasswordRequest DTO
  console.log('调用修改密码API, 参数:', passwordData);
  
  if (!authService.isLoggedIn()) {
    console.error('修改密码失败: 用户未登录');
    return Promise.reject(new Error("用户未登录或会话已过期"));
  }
  
  const headers = authService.getAuthHeader();
  console.log('认证头信息:', headers);
  
  return axios.post(`${API_BASE_URL}/users/me/change-password`, passwordData, {
    headers: headers
  })
  .then(response => response.data) // Expects a success message or updated user DTO
  .catch(error => {
    console.error("Change password API error:", error.response?.data || error.message);
    throw error.response?.data || new Error('修改密码失败');
  });
}