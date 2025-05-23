package com.example.yourproject.service;

import com.example.yourproject.model.Category;
import com.example.yourproject.dto.CategoryCreateRequest;
import com.example.yourproject.dto.CategoryDto;
import java.util.List;

public interface CategoryService {

    /**
     * 根据ID获取分类信息
     * @param categoryId 分类ID
     * @return Category对象，或null（如果未找到）
     */
    Category getCategoryById(Long categoryId);

    /**
     * 获取所有商品分类
     * (此方法适合使用缓存 @Cacheable)
     * @return 分类列表
     */
    List<Category> getAllCategories();

    /**
     * 创建一个新的商品分类
     * @param createRequest DTO 包含创建分类所需信息
     * @return 创建后的Category对象（包含生成的ID）
     * @throws RuntimeException 如果分类名已存在
     */
    Category createCategory(CategoryCreateRequest createRequest) throws RuntimeException;

    /**
     * 更新商品分类信息
     * @param categoryId 要更新的分类ID
     * @param categoryDto DTO 包含更新分类所需信息
     * @return 更新后的Category对象
     * @throws RuntimeException 如果分类不存在或分类名冲突
     */
    Category updateCategory(Long categoryId, CategoryDto categoryDto) throws RuntimeException;

    /**
     * 根据ID删除商品分类
     * @param categoryId 分类ID
     * @return 是否删除成功
     */
    boolean deleteCategory(Long categoryId);
}
