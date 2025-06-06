package com.example.yourproject.controller;

import com.example.yourproject.model.Category;
import com.example.yourproject.service.CategoryService;
import com.example.yourproject.dto.CategoryDto; // 需要创建
import com.example.yourproject.dto.CategoryCreateRequest; // 需要创建

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    private CategoryDto convertToDto(Category category) {
        if (category == null) return null;
        CategoryDto dto = new CategoryDto();
        dto.setCategoryId(category.getCategoryId());
        dto.setCategoryName(category.getCategoryName());
        dto.setDescription(category.getDescription());
        return dto;
    }

    // GET /api/categories - 获取所有分类 (公开)
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryDto> dtos = categories.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET /api/categories/{id} - 获取分类详情 (公开)
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDto(category));
    }

    // POST /api/categories - 创建新分类 (管理员权限，后续在SecurityConfig中配置)
    @PostMapping
    // @PreAuthorize("hasRole('ADMIN')") // 示例：后续添加权限控制
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryCreateRequest createRequest) {
        Category category = categoryService.createCategory(createRequest); // CategoryService需支持此方法
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(category));
    }

    // PUT /api/categories/{id} - 更新分类 (管理员权限)
    @PutMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDto categoryDto) {
         // 确保 categoryDto 中 id 与 path variable 一致或 service 层处理
        Category updatedCategory = categoryService.updateCategory(id, categoryDto); // CategoryService需支持此方法
        if (updatedCategory == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDto(updatedCategory));
    }

    // DELETE /api/categories/{id} - 删除分类 (管理员权限)
    @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) { // 例如，分类下有商品时不允许删除
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 或者其他合适的错误码
        }
    }
}