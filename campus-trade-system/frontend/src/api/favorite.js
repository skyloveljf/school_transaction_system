import axios from 'axios';
import authService from '../services/authService';
import { ElMessage } from 'element-plus';

const API_BASE_URL = '/api'; // Assuming Vite proxy

// 检查特定商品是否被当前用户收藏
// Corresponds to: GET /api/favorites/product/{productId}/status
export function isProductFavoritedApi(productId) {
  if (!authService.isLoggedIn()) {
    return Promise.reject(new Error('用户未登录'));
  }
  return axios.get(`${API_BASE_URL}/favorites/product/${productId}/status`, {
    headers: authService.getAuthHeader()
  })
  .then(response => response.data) // Expects boolean
  .catch(error => {
    console.error(`Is Product ${productId} Favorited API error:`, error.response?.data || error.message);
    throw error.response?.data || new Error('检查收藏状态失败');
  });
}

// 添加商品到收藏
// Corresponds to: POST /api/favorites (RequestBody: FavoriteRequest - just productId)
export function addFavoriteApi(productId) {
  if (!authService.isLoggedIn()) {
    return Promise.reject(new Error('用户未登录'));
  }
  return axios.post(`${API_BASE_URL}/favorites`, { productId }, { // Backend expects { "productId": ... }
    headers: authService.getAuthHeader()
  })
  .then(response => response.data) // Expects FavoriteDto
  .catch(error => {
    console.error(`Add Favorite ${productId} API error:`, error.response?.data || error.message);
    throw error.response?.data || new Error('添加收藏失败');
  });
}

// 从收藏中移除商品 (按商品ID)
// Corresponds to: DELETE /api/favorites/product/{productId}
export function removeFavoriteByProductIdApi(productId) {
  if (!authService.isLoggedIn()) {
    return Promise.reject(new Error('用户未登录'));
  }
  return axios.delete(`${API_BASE_URL}/favorites/product/${productId}`, {
    headers: authService.getAuthHeader()
  })
  .then(response => response.data) // Expects void or a success message
  .catch(error => {
    console.error(`Remove Favorite by Product ID ${productId} API error:`, error.response?.data || error.message);
    throw error.response?.data || new Error('取消收藏失败');
  });
}

// 从收藏中移除商品 (按收藏ID)
// Corresponds to: DELETE /api/favorites/{favoriteId}
export function removeFavoriteByFavoriteIdApi(favoriteId) {
  if (!authService.isLoggedIn()) {
    return Promise.reject(new Error('用户未登录'));
  }
  return axios.delete(`${API_BASE_URL}/favorites/${favoriteId}`, {
    headers: authService.getAuthHeader()
  })
  .then(response => response.data) // Expects void or a success message
  .catch(error => {
    console.error(`Remove Favorite by Favorite ID ${favoriteId} API error:`, error.response?.data || error.message);
    throw error.response?.data || new Error('取消收藏失败');
  });
}

// 获取当前用户的收藏列表
// Corresponds to: GET /api/favorites/my
export function getMyFavoritesApi() {
  if (!authService.isLoggedIn()) {
    console.warn("User not logged in, returning empty favorites list.");
    return Promise.resolve([]); // 对于未登录用户，直接返回空列表，不使用模拟数据
  }

  return axios.get(`${API_BASE_URL}/favorites/my`, {
    headers: authService.getAuthHeader()
  })
  .then(response => {
    if (response && Array.isArray(response.data)) {
      return response.data; // 后端应返回 List<FavoriteDto>
    }
    console.warn('Get My Favorites API did not return an array, returning empty list. Response:', response.data);
    return []; 
  })
  .catch(error => {
    console.error('Get My Favorites API error:', error.response?.data || error.message);
    // API调用失败也返回空列表，并在组件中提示用户
    ElMessage.error('加载收藏列表失败，请稍后重试。'); // 可选：在此处或组件中提示
    return []; 
  });
}
