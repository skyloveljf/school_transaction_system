import axios from 'axios';
import authService from '../services/authService';

const API_BASE_URL = '/api'; // 假设已配置 Vite 代理

// 获取所有商品列表（可带查询参数）
export function getAllProductsApi(params) {
  return axios.get(`${API_BASE_URL}/products`, { params })
    .then(response => response.data)
    .catch(error => {
      console.error("Get All Products API error:", error.response?.data || error.message);
      throw error.response?.data || new Error("获取商品列表失败");
    });
}

// 获取单个商品详情
export function getProductByIdApi(productId) {
  return axios.get(`${API_BASE_URL}/products/${productId}`)
    .then(response => response.data)
    .catch(error => {
      console.error(`Get Product By ID ${productId} API error:`, error.response?.data || error.message);
      throw error.response?.data || new Error("获取商品详情失败");
    });
}

// 获取指定用户发布的商品列表
export function getProductsByUserIdApi(userId) {
  if (!authService.isLoggedIn()) {
    return Promise.reject(new Error("用户未登录，无法获取其发布的商品"));
  }
  if (!userId) {
    return Promise.reject(new Error("无效的用户ID"));
  }

  return axios.get(`${API_BASE_URL}/products/user/${userId}`, {
    headers: authService.getAuthHeader()
  })
    .then(response => response.data)
    .catch(error => {
      console.error(`Get Products By User ID ${userId} API error:`, error.response?.data || error.message);
      throw error.response?.data || new Error("获取用户商品列表失败");
    });
}

// 发布新商品（需要认证）
export function createProductApi(productData) {
  if (!authService.isLoggedIn()) {
    return Promise.reject(new Error("用户未登录或会话已过期"));
  }

  const payload = {
    productName: productData.productName || productData.title,
    description: productData.description,
    price: productData.price,
    categoryId: productData.categoryId,
    imageUrl: productData.imageUrl,
  };

  return axios.post(`${API_BASE_URL}/products`, payload, {
    headers: authService.getAuthHeader()
  })
    .then(response => response.data)
    .catch(error => {
      console.error("Create Product API error:", error.response?.data || error.message);
      throw error.response?.data || new Error("发布商品失败");
    });
}

// 更新商品信息（需要认证）
export function updateProductApi(productId, productData) {
  if (!authService.isLoggedIn()) {
    return Promise.reject(new Error("用户未登录或会话已过期"));
  }

  return axios.put(`${API_BASE_URL}/products/${productId}`, productData, {
    headers: authService.getAuthHeader()
  })
    .then(response => response.data)
    .catch(error => {
      console.error(`Update Product ${productId} API error:`, error.response?.data || error.message);
      throw error.response?.data || new Error("更新商品失败");
    });
}

// 删除商品（需要认证）
export function deleteProductApi(productId) {
  if (!authService.isLoggedIn()) {
    return Promise.reject(new Error("用户未登录或会话已过期"));
  }

  return axios.delete(`${API_BASE_URL}/products/${productId}`, {
    headers: authService.getAuthHeader()
  })
    .then(response => response.data)
    .catch(error => {
      console.error(`Delete Product ${productId} API error:`, error.response?.data || error.message);
      throw error.response?.data || new Error("删除商品失败");
    });
}
