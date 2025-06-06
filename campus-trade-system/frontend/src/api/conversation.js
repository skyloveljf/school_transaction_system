import request from '@/utils/request'

/**
 * 获取当前用户的会话列表
 * @returns {Promise} - Promise对象
 */
export function getConversationsApi() {
  console.log('获取会话列表');
  return request({
    url: '/api/conversations',
    method: 'get'
  })
}

/**
 * 获取会话详情
 * @param {Number} conversationId - 会话ID
 * @returns {Promise} - Promise对象
 */
export function getConversationDetailApi(conversationId) {
  console.log(`获取会话详情 - ID: ${conversationId}`);
  return request({
    url: `/api/conversations/${conversationId}`,
    method: 'get'
  })
}

/**
 * 创建新会话或获取已有会话
 * @param {Number} receiverId - 接收者ID
 * @returns {Promise} - Promise对象
 */
export function createConversationApi(receiverId) {
  console.log(`创建会话 - 接收者ID: ${receiverId}`);
  return request({
    url: '/api/conversations',
    method: 'post',
    data: { receiverId }
  }).then(response => {
    console.log('创建会话响应:', response);
    return response;
  }).catch(error => {
    console.error('创建会话失败:', error);
    if (error.response) {
      console.error('错误状态:', error.response.status);
      console.error('错误数据:', error.response.data);
    }
    throw error;
  });
}

/**
 * 标记会话为已读
 * @param {number} conversationId - 会话ID
 */
export function markConversationAsReadApi(conversationId) {
  return request({
    url: `/api/conversations/${conversationId}/read`,
    method: 'put'
  })
} 