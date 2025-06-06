package com.example.yourproject.controller;

import com.example.yourproject.model.Product;
import com.example.yourproject.model.User;
import com.example.yourproject.model.Category;
import com.example.yourproject.model.enums.ProductStatus;
import com.example.yourproject.service.ProductService;
import com.example.yourproject.service.UserService; // 用于获取当前用户
import com.example.yourproject.dto.ProductCreateDTO; // 需要创建此DTO
import com.example.yourproject.dto.ProductUpdateRequest; // 需要创建此DTO
import com.example.yourproject.dto.ProductResponseDto; // 需要创建此DTO (用于列表和详情)

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal; // ProductCreateRequest 可能需要

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    @Autowired
    public ProductController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        return userService.getUserByUsername(currentPrincipalName); // 确保UserService有此方法
    }
    
    private ProductResponseDto convertToDto(Product product) {
        try {  // 添加try-catch块来捕获潜在的异常
            if (product == null) return null;
            ProductResponseDto dto = new ProductResponseDto();
            
            // 设置基本属性，增加空检查以避免NPE
            dto.setProductId(product.getProductId());
            dto.setTitle(product.getTitle() != null ? product.getTitle() : "");
            dto.setDescription(product.getDescription() != null ? product.getDescription() : "");
            dto.setPrice(product.getPrice());
            
            // 安全处理状态字段
            if (product.getStatus() != null) {
                dto.setStatus(product.getStatus().name());
            } else {
                dto.setStatus(null); 
            }
            
            dto.setImageUrl(product.getImageUrl());
            dto.setPostDate(product.getPostDate());
            dto.setViews(product.getViews() != null ? product.getViews() : 0);
            
            // 安全处理卖家信息
            if (product.getSeller() != null) {
                try {
                    dto.setSellerId(product.getSeller().getUserId());
                    dto.setSellerUsername(product.getSeller().getUsername());
                } catch (Exception e) {
                    System.err.println("Error processing seller data: " + e.getMessage());
                    // 在异常情况下，使用productId作为备用
                    dto.setSellerId(product.getUserId());
                    dto.setSellerUsername("Unknown User");
                }
            } else {
                // 如果seller为null但有userId，直接使用userId
                if (product.getUserId() != null) {
                    dto.setSellerId(product.getUserId());
                    dto.setSellerUsername("Unknown User");
                }
            }
            
            // 安全处理分类信息
            if (product.getCategory() != null) {
                try {
                    dto.setCategoryId(product.getCategory().getCategoryId());
                    dto.setCategoryName(product.getCategory().getCategoryName());
                } catch (Exception e) {
                    System.err.println("Error processing category data: " + e.getMessage());
                    // 在异常情况下，使用categoryId作为备用
                    dto.setCategoryId(product.getCategoryId());
                    dto.setCategoryName("Unknown Category");
                }
            } else {
                // 如果category为null但有categoryId，直接使用categoryId
                if (product.getCategoryId() != null) {
                    dto.setCategoryId(product.getCategoryId());
                    dto.setCategoryName("Unknown Category");
                }
            }
            
            return dto;
        } catch (Exception e) {
            // 记录异常信息并返回基本信息
            System.err.println("Error converting product to DTO: " + e.getMessage());
            e.printStackTrace();
            
            // 返回一个最基本的DTO
            ProductResponseDto basicDto = new ProductResponseDto();
            if (product != null) {
                basicDto.setProductId(product.getProductId());
                basicDto.setTitle("Error: Unable to load full product data");
            }
            return basicDto;
        }
    }

    // GET /api/products - 获取所有商品 (公开)
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "AVAILABLE") String status) { // 默认只查询可购买的
        List<Product> products = productService.getAllProductsWithDetails(ProductStatus.AVAILABLE);
        ProductStatus productStatus = null;
        try {
            if (status != null && !status.trim().isEmpty()){
                 productStatus = ProductStatus.valueOf(status.toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            //  可以记录日志或返回错误，这里简单忽略无效状态，依赖service层处理null
        }


        if (keyword != null || categoryId != null || minPrice != null || maxPrice != null) {
            // 注意：ProductService中的searchProducts方法需要支持这些参数
            products = productService.searchProducts(keyword, categoryId, minPrice, maxPrice, productStatus, true); // true 表示获取详细信息
        } else {
            products = productService.getAllProductsWithDetails(productStatus); // getAllProductsWithDetails 可能需要重载以接受 status
        }
        List<ProductResponseDto> productDtos = products.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(productDtos);
    }

    // GET /api/products/{id} - 获取商品详情 (公开)
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id); // getProductById 应该会增加浏览量
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDto(product));
    }

    // POST /api/products - 创建新商品 (需要认证)
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductCreateDTO createRequest) {
        User currentUser = getCurrentUser();
        Product product = productService.createProduct(createRequest, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(product));
    }

    // PUT /api/products/{id} - 更新商品信息 (需要认证，且为所有者)
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductUpdateRequest updateRequest) {
        User currentUser = getCurrentUser();
        Product updatedProduct = productService.updateProduct(id, updateRequest, currentUser);
        if (updatedProduct == null) {
            // 此处可能因为权限不足或商品不存在而返回null，service层应抛出异常以区分
            return ResponseEntity.notFound().build(); // 或者根据service层具体实现返回 403 或 404
        }
        return ResponseEntity.ok(convertToDto(updatedProduct));
    }

    // DELETE /api/products/{id} - 删除商品 (需要认证，且为所有者)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        productService.deleteProduct(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    // GET /api/products/user/{userId} - 获取某用户发布的商品列表 (公开)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByUserId(@PathVariable Long userId) {
        try {
            // 添加入口日志
            System.out.println("GET /api/products/user/" + userId + " - 开始处理请求");
            
            // 获取产品列表
            List<Product> products = productService.getProductsByUserId(userId);
            
            // 验证和过滤产品列表
            List<Product> validProducts = new java.util.ArrayList<>();
            if (products != null) {
                for (Product product : products) {
                    try {
                        // 对每个产品进行基本验证
                        if (product != null && product.getProductId() != null) {
                            // 确保product的关键属性不为null
                            if (product.getTitle() == null) product.setTitle("");
                            if (product.getDescription() == null) product.setDescription("");
                            if (product.getViews() == null) product.setViews(0);
                            
                            // 确保seller属性安全
                            if (product.getSeller() == null) {
                                System.out.println("Product " + product.getProductId() + " has null seller - creating empty one");
                                User emptySeller = new User();
                                emptySeller.setUserId(product.getUserId());
                                emptySeller.setUsername("Unknown");
                                product.setSeller(emptySeller);
                            }
                            
                            // 确保category属性安全
                            if (product.getCategory() == null) {
                                System.out.println("Product " + product.getProductId() + " has null category - creating empty one");
                                Category emptyCategory = new Category();
                                emptyCategory.setCategoryId(product.getCategoryId());
                                emptyCategory.setCategoryName("Unknown");
                                product.setCategory(emptyCategory);
                            }
                            
                            // 添加到有效产品列表
                            validProducts.add(product);
                        } else {
                            System.out.println("Skipping null or invalid product (id: " + 
                                (product != null ? product.getProductId() : "null") + ")");
                        }
                    } catch (Exception e) {
                        // 记录单个产品处理异常但继续处理其他产品
                        System.err.println("Error validating product: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            
            System.out.println("Found " + (products != null ? products.size() : 0) + 
                " products, " + validProducts.size() + " valid after filtering");
            
            // 转换为DTOs
            List<ProductResponseDto> dtos = new java.util.ArrayList<>();
            for (Product product : validProducts) {
                try {
                    ProductResponseDto dto = convertToDto(product);
                    if (dto != null) {
                        dtos.add(dto);
                    }
                } catch (Exception e) {
                    System.err.println("Error converting product to DTO: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("Successfully converted " + dtos.size() + " products to DTOs");
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            // 捕获整个方法的异常并记录
            System.err.println("Critical error in getProductsByUserId: " + e.getMessage());
            e.printStackTrace();
            
            // 返回空列表而不是错误，以避免前端崩溃
            return ResponseEntity.ok(new java.util.ArrayList<>());
        }
    }

    // GET /api/products/category/{categoryId} - 获取某分类下的商品列表 (公开)
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByCategoryId(@PathVariable Long categoryId) {
        List<Product> products = productService.getProductsByCategoryId(categoryId);
         List<ProductResponseDto> dtos = products.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}