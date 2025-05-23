### 模块：商品管理 (Product Module) - API 端点

#### 功能点：获取所有商品/搜索商品 (GetAllProducts)
*   **核心功能**: `GET /api/products`。公开接口，获取商品列表，支持关键字、分类ID、价格区间、状态过滤。默认查询AVAILABLE状态。
*   **主要交互**: 
    *   `ProductController.getAllProducts(...)`
    *   调用 `ProductService.searchProducts(...)` 或 `ProductService.getAllProductsWithDetails(...)`。
    *   将 `Product` 实体列表转为 `ProductResponseDto` 列表。
*   **依赖**: `ProductResponseDto`, `ProductService`。
*   **潜在问题/优化点**: 
    *   Controller中对无效`status`参数的处理，建议返回400。
    *   确保Service层方法签名与Controller调用匹配。
*   **已审查组件**: `ProductController` 中对应方法。

#### 功能点：获取单个商品详情 (GetProductById)
*   **核心功能**: `GET /api/products/{id}`。公开接口，根据ID获取商品详情，并增加浏览量。
*   **主要交互**:
    *   `ProductController.getProductById(@PathVariable Long id)`
    *   调用 `ProductService.getProductById(id)` (Service内部应调用Mapper增加浏览量)。
    *   处理商品未找到 (404)。
    *   将 `Product` 实体转为 `ProductResponseDto`。
*   **依赖**: `ProductResponseDto`, `ProductService`。
*   **潜在问题/优化点**: 确认Service层浏览量增加逻辑。
*   **已审查组件**: `ProductController` 中对应方法。

#### 功能点：创建新商品 (CreateProduct)
*   **核心功能**: `POST /api/products`。需要认证。创建新商品，自动生成描述（如果未提供）。
*   **主要交互**:
    *   `ProductController.createProduct(@Valid @RequestBody ProductCreateRequest createRequest)`
    *   `ProductController.getCurrentUser()` 获取当前用户。
    *   调用 `ProductService.createProduct(ProductCreateRequest, User)`。
    *   Service层处理分类校验、AI生成描述、设置默认值、持久化。
    *   返回201 Created和新商品的 `ProductResponseDto`。
*   **依赖**: `ProductCreateRequest`, `ProductResponseDto`, `User` (model), `ProductService`, `UserService` (for `getCurrentUser`), `CategoryMapper`, `ProductMapper`, `DeepSeekService`.
*   **潜在问题/优化点**:
    *   `ProductController.getCurrentUser()` 的具体实现需符合Spring Security配置。
    *   `ProductServiceImpl` 中 `@CacheEvict` 的 `key` 表达式可能需要调整。
    *   `ProductCreateRequest` 中是否应包含 `stock` 字段，Service中如何处理库存。
    *   确保 `DeepSeekService` 正确注入和使用。
*   **已审查组件**: `ProductController` 中对应方法, `ProductService.createProduct` (接口与实现)。

#### 功能点：更新商品信息 (UpdateProduct)
*   **核心功能**: `PUT /api/products/{id}`。需要认证，且所有者才能更新。更新指定商品信息。
*   **主要交互**:
    *   `ProductController.updateProduct(@PathVariable Long id, @Valid @RequestBody ProductUpdateRequest updateRequest)`
    *   `ProductController.getCurrentUser()` 获取当前用户。
    *   调用 `ProductService.updateProduct(Long, ProductUpdateRequest, User)`。
    *   Service层处理商品存在性校验、权限校验、新分类ID校验、字段更新、持久化。
    *   返回200 OK和更新后商品的 `ProductResponseDto`。
*   **依赖**: `ProductUpdateRequest`, `ProductResponseDto`, `User` (model), `ProductService`, `UserService`, `CategoryMapper`, `ProductMapper`.
*   **潜在问题/优化点**:
    *   Controller中对Service层返回null的判断分支可能多余（若全局异常处理完善）。
    *   Service层字段更新逻辑（title, description, imageUrl, price）的细节（空字符串、null值处理）需确认符合业务预期。
    *   **重要：`ProductUpdateRequest` 和 `ProductServiceImpl` 中目前缺少对商品库存 (stock) 的更新处理。**
*   **已审查组件**: `ProductController` 中对应方法, `ProductService.updateProduct` (接口与实现)。

#### 功能点：删除商品 (DeleteProduct)
*   **核心功能**: `DELETE /api/products/{id}`。需要认证，且所有者才能删除。删除指定商品。
*   **主要交互**:
    *   `ProductController.deleteProduct(@PathVariable Long id)`
    *   `ProductController.getCurrentUser()` 获取当前用户。
    *   调用 `ProductService.deleteProduct(Long, User)`。
    *   Service层处理商品存在性校验、权限校验、业务规则校验（是否有未完成订单）、持久化删除。
    *   返回204 No Content。
*   **依赖**: `User` (model), `ProductService`, `UserService`, `ProductMapper`, `OrderMapper`, `Order` (model), `OrderStatus` (enum).
*   **潜在问题/优化点**:
    *   确保 `OrderMapper.findByProductId` 及其实现存在且正确。
    *   确保 `Order` 实体及 `OrderStatus` 枚举支持业务规则校验。
*   **已审查组件**: `ProductController` 中对应方法, `ProductService.deleteProduct` (接口与实现)。 