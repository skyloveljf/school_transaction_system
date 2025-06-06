package com.example.yourproject.service.impl;

import com.example.yourproject.dto.CategoryCreateRequest;
import com.example.yourproject.dto.CategoryDto;
import com.example.yourproject.mapper.CategoryMapper;
import com.example.yourproject.mapper.ProductMapper;
import com.example.yourproject.model.Category;
import com.example.yourproject.model.Product;
import com.example.yourproject.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching; // Import for @Caching
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    @Autowired
    public CategoryServiceImpl(CategoryMapper categoryMapper, ProductMapper productMapper) {
        this.categoryMapper = categoryMapper;
        this.productMapper = productMapper;
    }

    @Override
    @Cacheable(value = "categories", key = "#categoryId")
    public Category getCategoryById(Long categoryId) { // Changed to Long
        System.out.println("Fetching category from DB by ID: " + categoryId);
        return categoryMapper.findById(categoryId); // Assumes categoryMapper.findById takes Long
    }

    @Override
    @Cacheable("allCategories")
    public List<Category> getAllCategories() {
        System.out.println("Fetching all categories from DB...");
        return categoryMapper.findAll();
    }

    @Override
    @Transactional
    // Use @Caching for multiple @CachePut or @CacheEvict on the same method
    @Caching(
        put = { @CachePut(value = "categories", key = "#result.categoryId") },
        evict = { @CacheEvict(value = "allCategories", allEntries = true) }
    )
    public Category createCategory(CategoryCreateRequest createRequest) throws RuntimeException { // Parameter changed to DTO
        Category existingCategory = categoryMapper.findByName(createRequest.getCategoryName());
        if (existingCategory != null) {
            throw new RuntimeException("分类名称已存在: " + createRequest.getCategoryName());
        }
        Category newCategory = new Category();
        newCategory.setCategoryName(createRequest.getCategoryName());
        newCategory.setDescription(createRequest.getDescription());
        
        categoryMapper.insert(newCategory); // MyBatis should populate the ID if configured
        return newCategory; // Return the category with ID
    }

    @Override
    @Transactional
    @Caching(
        put = { @CachePut(value = "categories", key = "#categoryId") }, // Use categoryId from parameter for key
        evict = { @CacheEvict(value = "allCategories", allEntries = true) }
    )
    public Category updateCategory(Long categoryId, CategoryDto categoryDto) throws RuntimeException { // Parameters changed
        Category existingCategoryById = categoryMapper.findById(categoryId); // Use Long categoryId
        if (existingCategoryById == null) {
            throw new RuntimeException("分类不存在: ID " + categoryId);
        }

        // Check for name change and conflict
        if (categoryDto.getCategoryName() != null && !categoryDto.getCategoryName().equals(existingCategoryById.getCategoryName())) {
            Category categoryWithNewName = categoryMapper.findByName(categoryDto.getCategoryName());
            if (categoryWithNewName != null && !categoryWithNewName.getCategoryId().equals(categoryId)) {
                throw new RuntimeException("分类名称 '" + categoryDto.getCategoryName() + "' 已被其他分类使用。");
            }
            existingCategoryById.setCategoryName(categoryDto.getCategoryName());
        }
        
        if (categoryDto.getDescription() != null) { // Allow description update
            existingCategoryById.setDescription(categoryDto.getDescription());
        }
        
        categoryMapper.update(existingCategoryById);
        return categoryMapper.findById(categoryId); // Return the updated category
    }

    @Override
    @Transactional
    @Caching(evict = { // Group multiple @CacheEvict
        @CacheEvict(value = "categories", key = "#categoryId"),
        @CacheEvict(value = "allCategories", allEntries = true)
    })
    public boolean deleteCategory(Long categoryId) { // Changed to Long
        Category category = categoryMapper.findById(categoryId); // Assumes categoryMapper.findById takes Long
        if (category == null) {
            throw new RuntimeException("要删除的分类不存在: ID " + categoryId);
        }

        // Important: Ensure productMapper.findByCategoryId also accepts Long
        List<Product> productsInCategory = productMapper.findByCategoryId(categoryId);
        if (productsInCategory != null && !productsInCategory.isEmpty()) {
            throw new RuntimeException("无法删除分类 '" + category.getCategoryName() + "'，因为它包含 " + productsInCategory.size() + " 个商品。");
        }

        int result = categoryMapper.deleteById(categoryId); // Assumes categoryMapper.deleteById takes Long
        return result > 0;
    }
}