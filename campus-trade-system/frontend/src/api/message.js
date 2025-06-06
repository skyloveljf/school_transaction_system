import request from '@/utils/request'

/**
 * 获取会话中的消息列表
 * @param {number} conversationId - 会话ID
 * @param {number} page - 页码
 * @param {number} size - 每页消息数
 */
export function getMessagesApi(conversationId, page = 0, size = 20) {
  console.log(`获取会话消息 - 会话ID: ${conversationId}, 页码: ${page}, 大小: ${size}`);
  return request({
    url: `/api/conversations/${conversationId}/messages`,
    method: 'get',
    params: { page, size }
  }).then(response => {
    console.log('获取消息列表成功:', response);
    return response;
  }).catch(error => {
    console.error('获取消息列表失败:', error);
    throw error;
  });
}

/**
 * 发送新消息
 * @param {number} conversationId - 会话ID
 * @param {string} content - 消息内容
 */
export function sendMessageApi(conversationId, content) {
  console.log(`发送消息: conversationId=${conversationId}, content=${content}`);
  return request({
    url: `/api/conversations/${conversationId}/messages`,
    method: 'post',
    data: { content }
  }).then(response => {
    console.log('发送消息成功:', response);
    return response;
  }).catch(error => {
    console.error('发送消息失败:', error);
    throw error;
  });
}

/**
 * 直接发送消息给用户（创建会话并发送）
 * @param {number} receiverId - 接收者用户ID
 * @param {string} content - 消息内容
 */
export function sendDirectMessageApi(conversationId, content) {
  console.log(`发送消息 - 会话ID: ${conversationId}, 内容: ${content}`);
  return request({
    url: `/api/conversations/${conversationId}/messages`,
    method: 'post',
    data: { content }
  }).then(response => {
    console.log('发送消息成功:', response);
    return response;
  }).catch(error => {
    console.error('发送消息失败:', error);
    throw error;
  });
}

/**
 * 标记指定消息为已读
 * @param {number} messageId - 消息ID
 */
export function markMessageAsReadApi(messageId) {
  return request({
    url: `/api/messages/${messageId}/read`,
    method: 'put'
  });
}

/**
 * 标记会话为已读
 * @param {Number} conversationId - 会话ID
 * @returns {Promise} - Promise对象
 */
export function markConversationAsReadApi(conversationId) {
  return request({
    url: `/api/conversations/${conversationId}/read`,
    method: 'put'
  });
}

/**
 * 获取未读消息数量
 */
export function getUnreadCountApi() {
  return request({
    url: '/api/messages/unread/count',
    method: 'get'
  });
} 