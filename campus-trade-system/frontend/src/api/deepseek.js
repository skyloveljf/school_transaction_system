import request from '../utils/request';

const API_BASE_URL = '/api';

/**
 * 调用AI生成商品描述
 * @param {string} productTitle - 商品名称
 * @returns {Promise<string>} - 返回生成的商品描述
 */
export function generateProductDescriptionApi(productTitle) {
  console.log('开始请求生成商品描述，商品名称:', productTitle);
  
  const params = new URLSearchParams();
  params.append('productTitle', productTitle);
  
  return request({
    url: `${API_BASE_URL}/ai/generate-description`,
    method: 'post',
    data: params,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
  .then(response => {
    console.log('生成描述成功，服务器返回:', response);
    
    // 后端现在返回正确的ApiResponse格式: {success: true, data: "AI生成的描述", message: "成功提示"}
    if (response && response.success && response.data) {
      console.log('成功提取AI生成的描述:', response.data.substring(0, 50) + '...');
      return response.data;
    }
    
    // 备用处理：如果格式不符合预期
    if (typeof response === 'string') {
      console.log('接收到字符串响应:', response);
      return response;
    }
    
    console.error('无法从响应中提取描述内容:', response);
    throw new Error('AI返回了无效的描述格式');
  })
  .catch(error => {
    console.error("生成商品描述API错误:", error.response ? error.response.data : error.message);
    
    if (error.response) {
      console.error("错误状态码:", error.response.status);
      console.error("错误响应数据:", error.response.data);
    }
    
    throw error.response && error.response.data && error.response.data.message ? 
      new Error(error.response.data.message) : 
      new Error('生成描述失败，请检查网络连接');
  });
}