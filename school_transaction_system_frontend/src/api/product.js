// src/api/products.js
import axios from 'axios';
import authService from '../services/authService';

const API_BASE_URL = '/api'; // Assuming Vite proxy is set up for /api

// 获取所有商品列表 (可带查询参数)
export function getAllProductsApi(params) { // params can be { keyword, categoryId, minPrice, maxPrice, status }
  return axios.get(`${API_BASE_URL}/products`, { params })
    .then(response => response.data) // Expects List<ProductResponseDto>
    .catch(error => {
      console.error("Get All Products API error:", error.response ? error.response.data : error.message);
      // 如果API调用失败，返回模拟数据以让前端能够正常工作
      console.log("使用模拟数据代替真实商品列表");
      return mockProducts;
    });
}

// 获取单个商品详情
export function getProductByIdApi(productId) {
  return axios.get(`${API_BASE_URL}/products/${productId}`)
    .then(response => response.data) // Expects ProductResponseDto
    .catch(error => {
      console.error(`Get Product By ID ${productId} API error:`, error.response ? error.response.data : error.message);
      // 尝试从模拟数据中找到对应ID的商品
      const mockProduct = mockProducts.find(p => p.id === parseInt(productId));
      if (mockProduct) {
        console.log(`使用ID为${productId}的模拟商品数据`);
        return mockProduct;
      }
      throw error.response ? error.response.data : new Error('获取商品详情失败');
    });
}

// 获取指定用户发布的商品列表
export function getProductsByUserIdApi(userId) {
  if (!authService.isLoggedIn()) {
    // 如果未登录，不应返回模拟数据，而是拒绝Promise或抛出错误
    console.warn("User not logged in, cannot fetch user-specific products.");
    return Promise.reject(new Error("用户未登录，无法获取其发布的商品")); 
  }
  // 确保 userId 是有效的，否则后端可能会出错
  if (!userId) {
    console.error("getProductsByUserIdApi called with invalid userId:", userId);
    return Promise.reject(new Error("无效的用户ID"));
  }

  return axios.get(`${API_BASE_URL}/products/user/${userId}`, {
    headers: authService.getAuthHeader()
  })
    .then(response => response.data) // Expects List<ProductResponseDto>
    .catch(error => {
      console.error(`Get Products By User ID ${userId} API error:`, error.response ? error.response.data : error.message);
      // 不再返回模拟数据，直接抛出错误
      throw error.response && error.response.data ? error.response.data : new Error(`获取用户 ${userId} 的商品列表失败`);
    });
}

// 发布新商品 (需要认证)
export function createProductApi(productData) {
  if (!authService.isLoggedIn()) {
    return Promise.reject(new Error("用户未登录或会话已过期"));
  }

  // 确保商品数据格式正确
  const payload = {
    productName: productData.productName || productData.title, // 支持两种键名
    description: productData.description,
    price: productData.price,
    categoryId: productData.categoryId,
    imageUrl: productData.imageUrl,
  };

  // 记录请求数据
  console.log('创建商品API请求数据:', payload);

  return axios.post(`${API_BASE_URL}/products`, payload, {
    headers: authService.getAuthHeader()
  })
  .then(response => {
    console.log('创建商品API响应:', response.data);
    return response.data; // Expects ProductResponseDto
  })
  .catch(error => {
    console.error("Create Product API error:", error.response ? error.response.data : error.message);
    
    // 如果服务器无法处理请求，我们可以记录模拟数据供调试使用
    // 但不要返回模拟数据，而是抛出错误，让调用者处理
    console.error('创建商品失败，请求数据:', payload);
    throw error.response ? error.response.data : new Error('发布商品失败');
  });
}

// 更新商品信息 (需要认证)
export function updateProductApi(productId, productData) {
  if (!authService.isLoggedIn()) {
    return Promise.reject(new Error("用户未登录或会话已过期"));
  }
  return axios.put(`${API_BASE_URL}/products/${productId}`, productData, {
    headers: authService.getAuthHeader()
  })
  .then(response => response.data) // Expects ProductResponseDto
  .catch(error => {
    console.error(`Update Product ${productId} API error:`, error.response ? error.response.data : error.message);
    throw error.response ? error.response.data : new Error('更新商品失败');
  });
}

// 删除商品 (需要认证)
export function deleteProductApi(productId) {
  if (!authService.isLoggedIn()) {
    return Promise.reject(new Error("用户未登录或会话已过期"));
  }
  return axios.delete(`${API_BASE_URL}/products/${productId}`, {
    headers: authService.getAuthHeader()
  })
  .then(response => response.data) // Expects 204 No Content or a confirmation message
  .catch(error => {
    console.error(`Delete Product ${productId} API error:`, error.response ? error.response.data : error.message);
    throw error.response ? error.response.data : new Error('删除商品失败');
  });
}

// 扩展模拟数据
export const mockProducts = [
  {
    id: 1,
    title: '大学高数教材全套',
    description: '九成新，适合大一学生，包含高数上下册和习题集。',
    image: '/media/product-images/math_books.jpg',
    price: 80.5,
    owner: 'user1',
    status: 'AVAILABLE'
  },
  {
    id: 2,
    title: '九成新二手笔记本电脑',
    description: 'i5处理器，8GB内存，256GB SSD，适合日常办公和学习。',
    image: '/media/product-images/laptop1.jpg',
    price: 1200,
    owner: 'user2',
    status: 'AVAILABLE'
  },
  {
    id: 3,
    title: '可爱猫咪玩偶',
    description: '柔软毛绒，治愈系玩具，适合送礼。',
    image: 'https://img.keaitupian.cn/uploads/upimg/1597372353577123.jpg',
    price: 39,
    owner: 'user1',
    status: 'AVAILABLE'
  },
  {
    id: 4,
    title: '小狗背包',
    description: '呆萌设计，轻便实用，深受学生喜爱。',
    image: 'https://cbu01.alicdn.com/img/ibank/2017/040/469/4086964040_756268226.jpg',
    price: 59,
    owner: 'user2',
    status: 'AVAILABLE'
  },
  {
    id: 5,
    title: '仓鼠水壶',
    description: '可爱造型，实用又好看，适合学生党。',
    image: 'https://th.bing.com/th/id/OIP.c_X-CKffYfKBanH1sq3igwHaHw?rs=1&pid=ImgDetMain',
    price: 29,
    owner: 'user3',
    status: 'AVAILABLE'
  }
];

export const mockComments = [
  {
    id: 1,
    productId: 1,
    userId: 101,
    username: '张三',
    content: '价格合适，感兴趣！',
    createdAt: '2023-05-01'
  },
  {
    id: 2,
    productId: 1,
    userId: 102,
    username: '李四',
    content: '图片不错，有货吗？',
    createdAt: '2023-05-02'
  },
  {
    id: 3,
    productId: 2,
    userId: 103,
    username: '王五',
    content: '能便宜点吗？',
    createdAt: '2023-05-03'
  }
];