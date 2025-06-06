package com.example.yourproject.service.impl;

import com.example.yourproject.dto.ProductCreateDTO;
import com.example.yourproject.dto.ProductUpdateRequest;
import com.example.yourproject.exception.BusinessRuleException;
import com.example.yourproject.exception.ResourceNotFoundException;
import com.example.yourproject.exception.UnauthorizedOperationException;
import com.example.yourproject.mapper.CategoryMapper;
import com.example.yourproject.mapper.CommentMapper;
import com.example.yourproject.mapper.FavoriteMapper;
import com.example.yourproject.mapper.OrderMapper;
import com.example.yourproject.mapper.ProductMapper;
import com.example.yourproject.model.*; 
import com.example.yourproject.model.enums.OrderStatus;
import com.example.yourproject.model.enums.ProductStatus;
import com.example.yourproject.service.ProductService;
import com.example.yourproject.service.DeepSeekService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.util.StringUtils; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final OrderMapper orderMapper;
    private final CommentMapper commentMapper;
    private final FavoriteMapper favoriteMapper;
    private final DeepSeekService deepSeekService;

    @Autowired
    public ProductServiceImpl(ProductMapper productMapper, CategoryMapper categoryMapper, OrderMapper orderMapper, 
                            CommentMapper commentMapper, FavoriteMapper favoriteMapper, DeepSeekService deepSeekService) {
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
        this.orderMapper = orderMapper;
        this.commentMapper = commentMapper;
        this.favoriteMapper = favoriteMapper;
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
    public List<Product> getAllProductsWithDetails(ProductStatus status) {
        if (status != null) {
             return productMapper.findAllWithDetailsByStatus(status.name()); 
        }
        return productMapper.findAllWithDetails();
    }

    @Override
    @Transactional
    public Product createProduct(ProductCreateDTO createRequest, User currentUser) {
        Category category = categoryMapper.findById(createRequest.getCategoryId());
        if (category == null) {
            throw new BusinessRuleException("Invalid Category ID: " + createRequest.getCategoryId());
        }

        Product product = new Product();
        product.setTitle(createRequest.getProductName());
        
        String description = createRequest.getDescription();
        if (!StringUtils.hasText(description)) {
            if (deepSeekService != null) {
                try {
                   logger.info("Description is empty for title '{}'. Attempting to generate with DeepSeek.", createRequest.getProductName());
                   description = deepSeekService.generateDescription(createRequest.getProductName());
                   if (!StringUtils.hasText(description)) {
                       logger.warn("DeepSeek generated an empty description for title '{}'. Using a default or keeping it empty.", createRequest.getProductName());
                   } else {
                        logger.info("Successfully generated description from DeepSeek: {}", description);
                   }
                } catch (Exception e) {
                    logger.error("Error generating description with DeepSeek for title '{}': {}", createRequest.getProductName(), e.getMessage(), e);
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
        product.setStatus(ProductStatus.PENDING_REVIEW);

        productMapper.insert(product);
        return productMapper.findById(product.getProductId()); 
    }

    @Override
    @Transactional
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
    public List<Product> getProductsByUserId(Long userId) {
        return productMapper.findByUserId(userId);
    }

    @Override
    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productMapper.findByCategoryId(categoryId);
    }

    @Override
    public List<Product> searchProducts(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, ProductStatus status, boolean fetchDetails) {
        return productMapper.searchProductsDetailed(keyword, categoryId, minPrice, maxPrice, (status != null ? status.name() : null));
    }

    @Override
    public List<Product> getAllProductsForAdmin() {
        return productMapper.findAllWithDetails();
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "products", allEntries = true),
        @CacheEvict(value = "productDetails", key = "#productId"),
        @CacheEvict(value = "categoryProducts", allEntries = true)
    })
    public boolean reviewProduct(Long productId, boolean approved) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }

        if (approved) {
            // 审核通过，设置为在售状态
            product.setStatus(ProductStatus.AVAILABLE);
            productMapper.update(product);
            logger.info("商品审核通过 ID: {}, 标题: {}", productId, product.getTitle());
            return true;
        } else {
            // 审核拒绝，级联删除商品及其相关数据
            logger.info("商品审核拒绝，准备删除 ID: {}, 标题: {}", productId, product.getTitle());
            
            try {
                // 1. 删除相关评论（包括回复）
                int deletedComments = commentMapper.deleteCommentsByProductId(productId);
                logger.info("删除了 {} 条相关评论", deletedComments);

                // 2. 删除相关收藏记录
                int deletedFavorites = favoriteMapper.deleteFavoritesByProductId(productId);
                logger.info("删除了 {} 条相关收藏记录", deletedFavorites);

                // 3. 删除相关订单
                int deletedOrders = orderMapper.deleteOrdersByProductId(productId);
                logger.info("删除了 {} 条相关订单", deletedOrders);

                // 4. 最后删除商品本身
                productMapper.deleteById(productId);
                
                logger.info("审核拒绝删除成功，总计清理：评论 {} 条，收藏 {} 条，订单 {} 条", 
                           deletedComments, deletedFavorites, deletedOrders);
                return true;
            } catch (Exception e) {
                logger.error("审核拒绝删除商品时发生异常: {}", e.getMessage(), e);
                throw new RuntimeException("审核拒绝删除商品失败：" + e.getMessage(), e);
            }
        }
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "products", allEntries = true),
        @CacheEvict(value = "productDetails", key = "#productId"),
        @CacheEvict(value = "categoryProducts", allEntries = true)
    })
    public boolean adminDeleteProduct(Long productId) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }

        logger.info("管理员删除商品 ID: {}, 标题: {}", productId, product.getTitle());

        try {
            // 1. 删除相关评论（包括回复）
            int deletedComments = commentMapper.deleteCommentsByProductId(productId);
            logger.info("删除了 {} 条相关评论", deletedComments);

            // 2. 删除相关收藏记录
            int deletedFavorites = favoriteMapper.deleteFavoritesByProductId(productId);
            logger.info("删除了 {} 条相关收藏记录", deletedFavorites);

            // 3. 删除相关订单
            int deletedOrders = orderMapper.deleteOrdersByProductId(productId);
            logger.info("删除了 {} 条相关订单", deletedOrders);

            // 4. 最后删除商品本身
            int deletedProducts = productMapper.deleteById(productId);
            
            if (deletedProducts > 0) {
                logger.info("商品删除成功，总计清理：评论 {} 条，收藏 {} 条，订单 {} 条", 
                           deletedComments, deletedFavorites, deletedOrders);
                return true;
            } else {
                logger.error("商品删除失败");
                return false;
            }
        } catch (Exception e) {
            logger.error("删除商品时发生异常: {}", e.getMessage(), e);
            throw new RuntimeException("删除商品失败：" + e.getMessage(), e);
        }
    }

    @Override
    public Long getAvailableProductCount() {
        return productMapper.countAvailableProducts();
    }

    @Override
    public List<java.util.Map<String, Object>> getCategoryProductStats() {
        return productMapper.getCategoryProductStats();
    }
}