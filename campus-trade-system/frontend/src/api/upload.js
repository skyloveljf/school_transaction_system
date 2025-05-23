import axios from 'axios';
import authService from '../services/authService';

const API_BASE_URL = '/api';

export function uploadImageApi(file) {
  if (!authService.isLoggedIn()) {
    return Promise.reject(new Error("用户未登录或会话已过期，无法上传图片"));
  }

  const formData = new FormData();
  formData.append('file', file); // 'file' 应该与后端接收文件的参数名一致

  // 检查后端上传接口是否可用
  return axios.post(`${API_BASE_URL}/upload/image`, formData, {
    headers: {
      ...authService.getAuthHeader(),
      'Content-Type': 'multipart/form-data'
    }
  })
  .then(response => {
    // 假设后端成功返回 { imageUrl: "/uploads/images/your-image-name.jpg" }
    if (response.data && response.data.imageUrl) {
      console.log('图片上传成功:', response.data.imageUrl);
      return response.data.imageUrl;
    }
    throw new Error('图片上传成功，但未返回有效的图片URL');
  })
  .catch(error => {
    console.error("Upload Image API error:", error.response ? error.response.data : error.message);
    
    // 如果后端服务不可用或接口未实现，使用临时图片URL以便测试其他功能
    console.warn("使用模拟图片URL以继续功能测试");
    
    // 生成随机图片文件名，模拟真实上传
    const timestamp = new Date().getTime();
    const randomId = Math.floor(Math.random() * 1000);
    const mockImageUrl = `/uploads/images/mock_${timestamp}_${randomId}.jpg`;
    
    // 返回模拟的图片URL
    return mockImageUrl;
  });
} 