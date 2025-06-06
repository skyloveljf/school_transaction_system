import request from '../utils/request'
import authService from '../services/authService'

// 获取普通用户列表（不包括管理员）
export const getRegularUsersApi = () => {
  return request({
    url: '/api/admin/regular-users',
    method: 'GET',
    headers: authService.getAuthHeader()
  })
}

// 删除用户
export const deleteUserApi = (userId) => {
  return request({
    url: `/api/admin/users/${userId}`,
    method: 'DELETE',
    headers: authService.getAuthHeader()
  })
}

// 获取所有用户列表（包括管理员）- 保留原有功能
export const getAllUsersApi = () => {
  return request({
    url: '/api/admin/users',
    method: 'GET',
    headers: authService.getAuthHeader()
  })
}

// 更新用户角色
export const updateUserRoleApi = (userId, role) => {
  return request({
    url: `/api/admin/users/${userId}/role`,
    method: 'PUT',
    params: { role },
    headers: authService.getAuthHeader()
  })
}

// 获取系统状态
export const getSystemStatusApi = () => {
  return request({
    url: '/api/admin/system-status',
    method: 'GET',
    headers: authService.getAuthHeader()
  })
}

// 获取所有评论列表
export const getAllCommentsApi = () => {
  return request({
    url: '/api/admin/comments',
    method: 'GET',
    headers: authService.getAuthHeader()
  })
}

// 删除评论
export const deleteCommentApi = (commentId) => {
  return request({
    url: `/api/admin/comments/${commentId}`,
    method: 'DELETE',
    headers: authService.getAuthHeader()
  })
}

// 获取所有商品列表（管理员功能）
export const getAllProductsApi = () => {
  return request({
    url: '/api/admin/products',
    method: 'GET',
    headers: authService.getAuthHeader()
  })
}

// 审核商品
export const reviewProductApi = (productId, approved) => {
  return request({
    url: `/api/admin/products/${productId}/review`,
    method: 'PUT',
    params: { approved },
    headers: authService.getAuthHeader()
  })
}

// 删除商品（下架）
export const deleteProductApi = (productId) => {
  return request({
    url: `/api/admin/products/${productId}`,
    method: 'DELETE',
    headers: authService.getAuthHeader()
  })
} 

// 获取管理员统计数据
export function getAdminStats() {
  return request({
    url: '/api/admin/stats',
    method: 'get',
    headers: authService.getAuthHeader()
  })
}

// ==================== 违禁规则管理 API ====================

// 获取所有违禁规则
export const getAllProhibitedRulesApi = () => {
  return request({
    url: '/api/admin/prohibited-rules',
    method: 'GET',
    headers: authService.getAuthHeader()
  })
}

// 获取激活的违禁规则
export const getActiveProhibitedRulesApi = () => {
  return request({
    url: '/api/admin/prohibited-rules/active',
    method: 'GET',
    headers: authService.getAuthHeader()
  })
}

// 创建违禁规则
export const createProhibitedRuleApi = (ruleName, ruleDescription) => {
  return request({
    url: '/api/admin/prohibited-rules',
    method: 'POST',
    params: { ruleName, ruleDescription },
    headers: authService.getAuthHeader()
  })
}

// 更新违禁规则
export const updateProhibitedRuleApi = (ruleId, ruleName, ruleDescription) => {
  return request({
    url: `/api/admin/prohibited-rules/${ruleId}`,
    method: 'PUT',
    params: { ruleName, ruleDescription },
    headers: authService.getAuthHeader()
  })
}

// 删除违禁规则
export const deleteProhibitedRuleApi = (ruleId) => {
  return request({
    url: `/api/admin/prohibited-rules/${ruleId}`,
    method: 'DELETE',
    headers: authService.getAuthHeader()
  })
}

// 更新违禁规则状态
export const updateProhibitedRuleStatusApi = (ruleId, isActive) => {
  return request({
    url: `/api/admin/prohibited-rules/${ruleId}/status`,
    method: 'PUT',
    params: { isActive },
    headers: authService.getAuthHeader()
  })
}

// ==================== 自动审核 API ====================

// 执行一键自动审核
export const executeAutoReviewApi = () => {
  return request({
    url: '/api/admin/products/auto-review',
    method: 'POST',
    headers: authService.getAuthHeader()
  })
}

// 审核单个商品（测试接口）
export const reviewSingleProductApi = (productId) => {
  return request({
    url: `/api/admin/products/${productId}/auto-review`,
    method: 'POST',
    headers: authService.getAuthHeader()
  })
}

// 获取自动审核历史记录
export const getAutoReviewHistoryApi = () => {
  return request({
    url: '/api/admin/products/auto-review/history',
    method: 'GET',
    headers: authService.getAuthHeader()
  })
}

// 清空自动审核历史记录
export const clearAutoReviewHistoryApi = () => {
  return request({
    url: '/api/admin/products/auto-review/history',
    method: 'DELETE',
    headers: authService.getAuthHeader()
  })
}

// 获取自动审核配置
export const getAutoReviewConfigApi = () => {
  return request({
    url: '/api/admin/products/auto-review/config',
    method: 'GET',
    headers: authService.getAuthHeader()
  })
}

// 更新自动审核配置
export const updateAutoReviewConfigApi = (enableAutoReview, reviewBatchSize, strictMode) => {
  return request({
    url: '/api/admin/products/auto-review/config',
    method: 'PUT',
    params: { enableAutoReview, reviewBatchSize, strictMode },
    headers: authService.getAuthHeader()
  })
}