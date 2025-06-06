package com.example.yourproject.mapper;

import com.example.yourproject.model.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {

    Category findById(@Param("categoryId") Long categoryId);

    Category findByName(@Param("categoryName") String categoryName);

    List<Category> findAll();

    int insert(Category category);

    int update(Category category);

    int deleteById(@Param("categoryId") Long categoryId);
} 