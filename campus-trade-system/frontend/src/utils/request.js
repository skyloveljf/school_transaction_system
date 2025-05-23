import axios from 'axios'
import { ElMessage } from 'element-plus'
import authService from '@/services/authService'

// 是否显示详细调试信息
const DEBUG = process.env.NODE_ENV === 'development';

// 创建axios实例
const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API || '',
  timeout: 15000 // 请求超时时间
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    if (DEBUG) {
      console.log('Request interceptor activated for:', config.url);
      console.log('请求URL:', config.url);
      console.log('请求方法:', config.method);
      console.log('请求参数:', config.method === 'get' ? config.params : config.data);
    }
    
    // 使用authService获取token并添加到请求头
    const token = authService.getToken();
    if (token) {
      if (DEBUG) {
        console.log(`Found token in localStorage: ${token.substring(0, 15)}...`);
        console.log(`Adding token to request headers for ${config.url}`);
      }
      // 确保Authorization头格式正确
      config.headers['Authorization'] = `Bearer ${token}`;
    } else {
      if (DEBUG) {
        console.warn(`No token available for request to ${config.url}`);
        console.warn('用户可能未登录，请确认登录状态');
      }
    }
    
    if (DEBUG) {
      console.log('最终请求配置:', {
        url: config.url,
        method: config.method,
        headers: config.headers,
        params: config.params,
        data: config.data
      });
    }
    
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    if (DEBUG) {
      console.log(`Response from ${response.config.url}:`, response.status);
      console.log('Response data:', res);
    }
    
    // 兼容多种后端返回格式
    
    // 标准格式: { success: true, data: {...}, message: "xxx" } 
    if (res && res.data !== undefined) {
      return res.data;
    }
    
    // 情况1: 直接返回数据对象 
    // 例如 { id: 1, name: 'xxx' }
    if (res) {
      return res;
    }
    
    // 默认返回完整响应
    return response;
  },
  error => {
    console.error('Response error intercepted:', error);
    
    // 打印更详细的错误信息
    if (error.response) {
      console.error('错误状态:', error.response.status);
      console.error('错误数据:', error.response.data);
      console.error('请求配置:', error.config);
      
      // 处理401未授权错误
      if (error.response.status === 401) {
        console.error('发现401错误 - 用户未登录或登录已过期');
        ElMessage.error('用户未登录或登录已过期，请重新登录')
        
        // 检查是否需要重定向到登录页
        const isLoginPage = window.location.pathname === '/' || 
                            window.location.pathname.includes('/login');
        
        if (!isLoginPage) {
          console.log('非登录页面收到401错误，准备重定向到登录页...');
          // 保存当前URL，以便登录后返回
          localStorage.setItem('redirectAfterLogin', window.location.pathname);
          // 延迟跳转，给用户一点时间看到错误消息
          setTimeout(() => {
            window.location.href = '/';
          }, 1500);
        }
      } 
      // 处理403禁止访问错误
      else if (error.response.status === 403) {
        ElMessage.error('没有权限执行此操作')
      }
      // 处理404错误
      else if (error.response.status === 404) {
        ElMessage.error('请求的资源不存在')
      }
      // 处理500服务器错误
      else if (error.response.status >= 500) {
        ElMessage.error('服务器错误，请稍后再试')
      }
      // 其他错误
      else {
        let message = '请求失败';
        if (error.response.data && error.response.data.message) {
          message = error.response.data.message;
        }
        ElMessage.error(message)
      }
    } else if (error.request) {
      // 请求已发送但没有收到响应
      console.error('请求未收到响应:', error.request)
      ElMessage.error('服务器无响应，请检查网络连接')
    } else {
      // 请求配置有误
      console.error('请求错误:', error.message)
      ElMessage.error('请求发送失败: ' + error.message)
    }
    
    return Promise.reject(error)
  }
)

export default service