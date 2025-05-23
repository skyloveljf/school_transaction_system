package com.example.yourproject.service.impl;

import com.example.yourproject.dto.ProductCreateDTO;
import com.example.yourproject.dto.ProductUpdateRequest;
import com.example.yourproject.exception.BusinessRuleException;
import com.example.yourproject.exception.ResourceNotFoundException;
import com.example.yourproject.exception.UnauthorizedOperationException;
import com.example.yourproject.mapper.CategoryMapper;
import com.example.yourproject.mapper.OrderMapper;
import com.example.yourproject.mapper.ProductMapper;
import com.example.yourproject.model.*; 
import com.example.yourproject.model.enums.OrderStatus;
import com.example.yourproject.model.enums.ProductStatus;
import com.example.yourproject.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.util.StringUtils; 

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.example.yourproject.service.DeepSeekService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final OrderMapper orderMapper;
    private final DeepSeekService deepSeekService;

    private static final String CACHE_PRODUCT_BY_ID = "productById";
    private static final String CACHE_ALL_PRODUCTS_DETAILS = "allProductsDetails";
    private static final String CACHE_PRODUCTS_BY_USER_ID = "productsByUserId";
    private static final String CACHE_PRODUCTS_BY_CATEGORY_ID = "productsByCategoryId";

    @Autowired
    public ProductServiceImpl(ProductMapper productMapper, CategoryMapper categoryMapper, OrderMapper orderMapper, DeepSeekService deepSeekService) {
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
        this.orderMapper = orderMapper;
        this.deepSeekService = deepSeekService;
    }

    @Override
    @Transactional 
    public Product getProductById(Long productId) {
        productMapper.incrementViews(productId);
        Product product = productMapper.findById(productId); 
        if (product == null) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
        return product;
    }

    @Override
    // @Cacheable(value = CACHE_ALL_PRODUCTS_DETAILS, key = "#status?.name() ?: 'ALL'")
    public List<Product> getAllProductsWithDetails(ProductStatus status) {
        if (status != null) {
             return productMapper.findAllWithDetailsByStatus(status.name()); 
        }
        return productMapper.findAllWithDetails();
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CACHE_ALL_PRODUCTS_DETAILS, allEntries = true),
            @CacheEvict(value = CACHE_PRODUCTS_BY_CATEGORY_ID, key = "#result.categoryId"), 
            @CacheEvict(value = CACHE_PRODUCTS_BY_USER_ID, key = "#result.userId")
    })
    public Product createProduct(ProductCreateDTO createRequest, User currentUser) {
        Category category = categoryMapper.findById(createRequest.getCategoryId());
        if (category == null) {
            throw new BusinessRuleException("Invalid Category ID: " + createRequest.getCategoryId());
        }

        Product product = new Product();
        product.setTitle(createRequest.getProductName());
        
        String description = createRequest.getDescription();
        if (!StringUtils.hasText(description)) { // StringUtils.hasText 检查 null, empty, 和纯空格
            if (deepSeekService != null) { // 确保 deepSeekService 已注入
                try {
                   logger.info("Description is empty for title '{}'. Attempting to generate with DeepSeek.", createRequest.getProductName());
                   description = deepSeekService.generateDescription(createRequest.getProductName());
                   if (!StringUtils.hasText(description)) {
                       logger.warn("DeepSeek generated an empty description for title '{}'. Using a default or keeping it empty.", createRequest.getProductName());
                       // Optionally set a default description if generation fails or returns empty
                       // description = "No description available."; 
                   } else {
                        logger.info("Successfully generated description from DeepSeek: {}", description);
                   }
                } catch (Exception e) {
                    // Log error and proceed without generated description
                    logger.error("Error generating description with DeepSeek for title '{}': {}", createRequest.getProductName(), e.getMessage(), e);
                    // description = "Error generating description."; // Or keep it as per original request
                }
            } else {
                 logger.warn("DeepSeekService not available, description will remain as provided (empty).");
            }
        }
        product.setDescription(description);
        product.setPrice(createRequest.getPrice());
        product.setCategoryId(createRequest.getCategoryId());
        product.setUserId(currentUser.getUserId());
        product.setImageUrl(createRequest.getImageUrl()); 

        product.setPostDate(LocalDateTime.now());
        product.setViews(0);
        product.setStatus(ProductStatus.AVAILABLE); 

        productMapper.insert(product);
        return productMapper.findById(product.getProductId()); 
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CACHE_PRODUCT_BY_ID, key = "#productId"),
            @CacheEvict(value = CACHE_ALL_PRODUCTS_DETAILS, allEntries = true),
            @CacheEvict(value = CACHE_PRODUCTS_BY_CATEGORY_ID, allEntries = true),
            @CacheEvict(value = CACHE_PRODUCTS_BY_USER_ID, allEntries = true)
    })
    public Product updateProduct(Long productId, ProductUpdateRequest updateRequest, User currentUser) {
        Product existingProduct = productMapper.findById(productId);
        if (existingProduct == null) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }

        if (!Objects.equals(existingProduct.getUserId(), currentUser.getUserId())) {
            throw new UnauthorizedOperationException("User not authorized to update this product.");
        }

        if (updateRequest.getCategoryId() != null && !updateRequest.getCategoryId().equals(existingProduct.getCategoryId())) {
            Category category = categoryMapper.findById(updateRequest.getCategoryId());
            if (category == null) {
                throw new BusinessRuleException("Invalid new Category ID: " + updateRequest.getCategoryId());
            }
            existingProduct.setCategoryId(updateRequest.getCategoryId());
        }

        if (StringUtils.hasText(updateRequest.getTitle())) {
            existingProduct.setTitle(updateRequest.getTitle());
        }
        if (updateRequest.getDescription() != null) { 
            existingProduct.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getPrice() != null) {
            existingProduct.setPrice(updateRequest.getPrice());
        }
        if (updateRequest.getImageUrl() != null) { 
            existingProduct.setImageUrl(updateRequest.getImageUrl());
        }
        if (updateRequest.getStatus() != null) {
            existingProduct.setStatus(updateRequest.getStatus());
        }

        productMapper.update(existingProduct);
        return productMapper.findById(existingProduct.getProductId());
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CACHE_PRODUCT_BY_ID, key = "#productId"),
            @CacheEvict(value = CACHE_ALL_PRODUCTS_DETAILS, allEntries = true),
            @CacheEvict(value = CACHE_PRODUCTS_BY_CATEGORY_ID, allEntries = true),
            @CacheEvict(value = CACHE_PRODUCTS_BY_USER_ID, allEntries = true)
    })
    public void deleteProduct(Long productId, User currentUser) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }

        if (!Objects.equals(product.getUserId(), currentUser.getUserId())) {
            throw new UnauthorizedOperationException("User not authorized to delete this product.");
        }

        List<Order> orders = orderMapper.findByProductId(productId);
        if (orders != null) {
            for (Order order : orders) {
                if (order.getStatus() != OrderStatus.COMPLETED &&
                    order.getStatus() != OrderStatus.CANCELLED && 
                    order.getStatus() != OrderStatus.REFUNDED) {
                    throw new BusinessRuleException("Cannot delete product. It has active or pending orders (Order ID: " + order.getOrderId() + ", Status: " + order.getStatus().name() + ")");
                }
            }
        }
        productMapper.deleteById(productId);
    }

    @Override
    @Cacheable(value = CACHE_PRODUCTS_BY_USER_ID, key = "#userId")
    public List<Product> getProductsByUserId(Long userId) {
        return productMapper.findByUserId(userId);
    }

    @Override
    @Cacheable(value = CACHE_PRODUCTS_BY_CATEGORY_ID, key = "#categoryId")
    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productMapper.findByCategoryId(categoryId);
    }

    @Override
    public List<Product> searchProducts(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, ProductStatus status, boolean fetchDetails) {
        return productMapper.searchProductsDetailed(keyword, categoryId, minPrice, maxPrice, (status != null ? status.name() : null));
    }
}
