import request from '../utils/request'; // 导入封装的axios实例
import authService from '../services/authService'; // 如果需要认证，则导入

/**
 * 获取所有商品分类
 * @returns {Promise<Array<{categoryId: number, categoryName: string, description: string}>>}
 */
export const getCategoriesApi = () => {
  return request({
    url: '/api/categories', // 后端获取分类的API端点
    method: 'GET',
    // 一般获取分类列表不需要特定的认证头，但如果您的API设计如此，可以取消下面的注释
    // headers: authService.getAuthHeader()
  });
};

// 如果未来需要添加其他与分类相关的API（如创建、更新、删除分类），可以在此文件继续添加
// 例如:
// export const createCategoryApi = (categoryData) => {
//   return request({
//     url: '/api/categories',
//     method: 'POST',
//     data: categoryData,
//     headers: authService.getAuthHeader() // 创建操作通常需要认证
//   });
// };