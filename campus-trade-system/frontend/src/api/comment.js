// src/api/comment.js
import axios from 'axios';
import authService from '../services/authService';

const API_BASE_URL = '/api'; // 使用Vite代理

/**
 * 获取商品的所有评论
 * @param {number} productId 商品ID
 * @returns {Promise<Array>} 评论列表
 */
export function getProductCommentsApi(productId) {
  return axios.get(`${API_BASE_URL}/comments/product/${productId}`)
    .then(response => response.data.data) // 返回评论数组
    .catch(error => {
      console.error('获取评论失败:', error.response ? error.response.data : error.message);
      throw error.response ? error.response.data : new Error('获取评论失败');
    });
}

/**
 * 添加新评论
 * @param {Object} commentData 评论数据 {productId, content, parentId?}
 * @returns {Promise<Object>} 创建的评论
 */
export function addCommentApi(commentData) {
  if (!authService.isLoggedIn()) {
    return Promise.reject(new Error('请先登录'));
  }

  return axios.post(`${API_BASE_URL}/comments`, commentData, {
    headers: authService.getAuthHeader()
  })
    .then(response => response.data.data) // 返回创建的评论
    .catch(error => {
      console.error('发布评论失败:', error.response ? error.response.data : error.message);
      throw error.response ? error.response.data : new Error('发布评论失败');
    });
}

/**
 * 删除评论
 * @param {number} commentId 评论ID
 * @returns {Promise<void>}
 */
export function deleteCommentApi(commentId) {
  if (!authService.isLoggedIn()) {
    return Promise.reject(new Error('请先登录'));
  }

  return axios.delete(`${API_BASE_URL}/comments/${commentId}`, {
    headers: authService.getAuthHeader()
  })
    .then(response => response.data) // 返回API响应
    .catch(error => {
      console.error('删除评论失败:', error.response ? error.response.data : error.message);
      throw error.response ? error.response.data : new Error('删除评论失败');
    });
} 