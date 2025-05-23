// 图片URL处理工具

/**
 * 确保图片URL包含完整的后端服务器地址
 * 这个工具函数解决图片路径问题：
 * - 如果图片路径已经是完整URL（以http://或https://开头），则直接返回
 * - 如果图片路径以/uploads/开头，则添加后端服务器地址前缀
 * - 如果图片路径为空或无效，返回默认图片路径
 */
export function getImageUrl(imageUrl) {
  // 默认图片URL，当传入的URL为空或无效时使用
  const DEFAULT_IMAGE = '/default-placeholder.png';
  
  // 如果没有提供图片URL，返回默认图片
  if (!imageUrl) {
    return DEFAULT_IMAGE;
  }
  
  // 如果已经是完整的URL（以http://或https://开头），则直接返回
  if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
    return imageUrl;
  }
  
  // 如果是/uploads/开头的相对路径，添加后端服务器地址前缀
  if (imageUrl.startsWith('/uploads/')) {
    // 使用配置的API_BASE_URL或硬编码的后端服务器地址
    // 在生产环境中，最好从配置中获取这个地址
    const BACKEND_URL = 'http://localhost:8080'; // 你的后端服务器地址
    return `${BACKEND_URL}${imageUrl}`;
  }
  
  // 对于其他情况，可能是相对于前端服务器的路径，直接返回
  return imageUrl;
} 