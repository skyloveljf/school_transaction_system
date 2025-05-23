\
# 校园二手交易系统后端功能模块

## 1. 用户认证模块 (AuthController)

### 1.1 用户注册
- **路径**: `POST /api/auth/register`
- **功能描述**: 接收用户名、密码、邮箱进行新用户注册。服务端会对密码进行加密处理。成功后返回创建成功的用户信息。
- **请求参数**: `UserRegisterRequest` (包含 `username`, `password`, `email`)
- **成功响应**: `UserProfileDto` (包含用户基本信息)

### 1.2 用户登录
- **路径**: `POST /api/auth/login`
- **功能描述**: 接收用户名和密码进行登录认证。认证成功后，生成 JWT (JSON Web Token) 并返回给客户端，同时更新用户最后登录时间。
- **请求参数**: `UserLoginRequest` (包含 `username`, `password`)
- **成功响应**: `AuthResponse` (包含 `token` 和 `UserProfileDto`)

## 2. 商品分类模块 (CategoryController)

### 2.1 获取所有分类
- **路径**: `GET /api/categories`
- **功能描述**: 返回系统中所有商品分类的列表。
- **成功响应**: `List<CategoryDto>`

### 2.2 获取分类详情
- **路径**: `GET /api/categories/{id}`
- **功能描述**: 根据分类 ID 返回特定商品分类的详细信息。
- **路径参数**: `id` (分类ID)
- **成功响应**: `CategoryDto`

### 2.3 创建新分类 (管理员)
- **路径**: `POST /api/categories`
- **功能描述**: 创建一个新的商品分类。此接口通常需要管理员权限。
- **请求参数**: `CategoryCreateRequest` (包含分类名称、描述等)
- **成功响应**: `CategoryDto`

### 2.4 更新分类 (管理员)
- **路径**: `PUT /api/categories/{id}`
- **功能描述**: 更新指定 ID 的商品分类信息。此接口通常需要管理员权限。
- **路径参数**: `id` (分类ID)
- **请求参数**: `CategoryDto` (包含更新后的分类信息)
- **成功响应**: `CategoryDto`

### 2.5 删除分类 (管理员)
- **路径**: `DELETE /api/categories/{id}`
- **功能描述**: 删除指定 ID 的商品分类。此接口通常需要管理员权限，并可能需要处理该分类下存在商品时的情况。
- **路径参数**: `id` (分类ID)
- **成功响应**: `204 No Content`

## 3. 用户收藏夹模块 (FavoriteController)

### 3.1 添加商品到收藏
- **路径**: `POST /api/favorites`
- **功能描述**: 当前登录用户将指定商品添加到自己的收藏夹。需要用户认证。
- **请求参数**: `FavoriteRequest` (包含 `productId`)
- **成功响应**: `FavoriteDto` (包含收藏记录信息)

### 3.2 从收藏中移除商品 (按收藏ID)
- **路径**: `DELETE /api/favorites/{favoriteId}`
- **功能描述**: 当前登录用户从自己的收藏夹中移除指定的收藏项。需要用户认证，并校验操作者是否为收藏的拥有者。
- **路径参数**: `favoriteId` (收藏记录ID)
- **成功响应**: `204 No Content`

### 3.3 获取当前用户的收藏列表
- **路径**: `GET /api/favorites/my`
- **功能描述**: 返回当前登录用户的所有收藏商品列表。需要用户认证。
- **成功响应**: `List<FavoriteDto>`

### 3.4 检查商品是否被当前用户收藏
- **路径**: `GET /api/favorites/product/{productId}/status`
- **功能描述**: 检查当前登录用户是否已收藏了指定的商品。需要用户认证。
- **路径参数**: `productId` (商品ID)
- **成功响应**: `Boolean` (true 表示已收藏, false 表示未收藏)

### 3.5 获取商品收藏总数
- **路径**: `GET /api/favorites/product/{productId}/count`
- **功能描述**: 返回指定商品被收藏的总次数。此接口通常公开。
- **路径参数**: `productId` (商品ID)
- **成功响应**: `Long` (收藏数量)

## 4. 订单管理模块 (OrderController)

### 4.1 创建订单
- **路径**: `POST /api/orders`
- **功能描述**: 当前登录用户为某个商品创建订单。需要用户认证。
- **请求参数**: `OrderCreateRequest` (包含 `productId`)
- **成功响应**: `OrderResponseDto` (包含新创建的订单详情)

### 4.2 获取订单详情
- **路径**: `GET /api/orders/{id}`
- **功能描述**: 获取指定 ID 的订单详细信息。需要用户认证，并且只有订单的买家或卖家才能查看。
- **路径参数**: `id` (订单ID)
- **成功响应**: `OrderResponseDto`

### 4.3 获取当前用户的购买订单列表
- **路径**: `GET /api/orders/my-purchases`
- **功能描述**: 返回当前登录用户作为买家的所有订单列表。需要用户认证。
- **成功响应**: `List<OrderResponseDto>`

### 4.4 获取当前用户的销售订单列表
- **路径**: `GET /api/orders/my-sales`
- **功能描述**: 返回当前登录用户作为卖家的所有订单列表。需要用户认证。
- **成功响应**: `List<OrderResponseDto>`

### 4.5 更新订单状态
- **路径**: `PATCH /api/orders/{id}/status`
- **功能描述**: 更新指定订单的状态 (例如：待付款、已付款、已发货、已完成、已取消)。需要用户认证，并根据用户角色 (买家/卖家) 和订单当前状态进行权限控制。
- **路径参数**: `id` (订单ID)
- **请求参数**: `OrderStatusUpdateRequest` (包含新的 `status`)
- **成功响应**: `OrderResponseDto` (更新后的订单详情)

## 5. 商品管理模块 (ProductController)

### 5.1 获取所有商品/搜索商品
- **路径**: `GET /api/products`
- **功能描述**: 获取商品列表，支持按关键字、分类ID、价格区间、商品状态 (默认为 `AVAILABLE` 表示在售) 进行筛选和搜索。
- **查询参数**: `keyword` (可选, 字符串), `categoryId` (可选, 长整型), `minPrice` (可选, 数字), `maxPrice` (可选, 数字), `status` (可选, 字符串, 如 AVAILABLE, SOLD, PENDING)
- **成功响应**: `List<ProductResponseDto>`

### 5.2 获取商品详情
- **路径**: `GET /api/products/{id}`
- **功能描述**: 获取指定 ID 的商品详细信息。调用此接口可能会增加商品浏览量。
- **路径参数**: `id` (商品ID)
- **成功响应**: `ProductResponseDto`

### 5.3 发布新商品
- **路径**: `POST /api/products`
- **功能描述**: 当前登录用户发布新的二手商品。需要用户认证。
- **请求参数**: `ProductCreateDTO` (包含商品标题、描述、价格、分类ID、图片URL等)
- **成功响应**: `ProductResponseDto` (新发布的商品详情)

### 5.4 更新商品信息
- **路径**: `PUT /api/products/{id}`
- **功能描述**: 当前登录用户更新自己发布的商品信息。需要用户认证，并且是商品的所有者。
- **路径参数**: `id` (商品ID)
- **请求参数**: `ProductUpdateRequest` (包含待更新的商品信息)
- **成功响应**: `ProductResponseDto` (更新后的商品详情)

### 5.5 删除商品
- **路径**: `DELETE /api/products/{id}`
- **功能描述**: 当前登录用户删除自己发布的商品。需要用户认证，并且是商品的所有者。
- **路径参数**: `id` (商品ID)
- **成功响应**: `204 No Content`

### 5.6 获取指定用户发布的商品列表
- **路径**: `GET /api/products/user/{userId}`
- **功能描述**: 查看指定用户 ID 发布的所有商品列表。此接口通常公开。
- **路径参数**: `userId` (用户ID)
- **成功响应**: `List<ProductResponseDto>`

### 5.7 获取指定分类下的商品列表
- **路径**: `GET /api/products/category/{categoryId}`
- **功能描述**: 查看指定分类 ID 下的所有商品列表。此接口通常公开。
- **路径参数**: `categoryId` (分类ID)
- **成功响应**: `List<ProductResponseDto>`

## 6. 用户个人资料模块 (UserController)

### 6.1 获取当前登录用户的个人资料
- **路径**: `GET /api/users/me`
- **功能描述**: 返回当前已认证用户的详细个人信息。
- **成功响应**: `UserProfileDto`

### 6.2 更新当前登录用户的个人资料
- **路径**: `PUT /api/users/me`
- **功能描述**: 更新当前已认证用户的个人信息 (例如邮箱等，具体可更新字段取决于后端实现)。
- **请求参数**: `UserUpdateRequest` (包含待更新的用户信息)
- **成功响应**: `UserProfileDto` (更新后的用户资料)

## 7. 辅助及基础设施
- **Spring Boot**: 核心框架
- **Spring Security**: 安全认证与授权
- **MyBatis**: ORM 框架，数据库交互
- **Redis**: 缓存
- **jjwt**: JSON Web Token 生成与校验
- **Springdoc OpenAPI**: API 文档生成
- **达梦数据库**: 使用的数据库类型
- **Lombok**: 简化 Java Bean 开发
- **Maven**: 项目构建和依赖管理

## 8. 文件上传服务 (FileUploadService)

### 8.1 上传图片
- **路径**: `POST /api/upload/image`
- **功能描述**: 接收图片文件，保存到服务器的指定位置 (例如 `uploads/images/` 目录下，相对于应用根目录或特定配置的静态资源目录)，并返回该图片的公共可访问URL。此接口需要用户认证。
- **请求类型**: `multipart/form-data`
- **请求参数**: `file` (表单中的文件字段名，类型为图片文件)
- **成功响应**: JSON 对象，包含图片的URL。例如: `{ "imageUrl": "/uploads/images/xxxxxxxx.jpg" }` (URL路径根据实际静态资源配置而定)
- **错误响应**:
    - 400 Bad Request: 如果文件为空、文件类型不支持、文件过大等。
    - 401 Unauthorized: 如果用户未认证。
    - 500 Internal Server Error: 如果服务器端处理文件失败。

