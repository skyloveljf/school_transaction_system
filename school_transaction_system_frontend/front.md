# 校园二手交易系统前端功能分析 (基于Vue3 + Element Plus)

## 1. 路由与主要视图

- **LoginRegister.vue (`/`)**: 用户登录与注册页面。
- **UserHome.vue (`/userhome`)**: 用户主页，聚合商品浏览、我的发布、我的收藏、个人信息等功能模块。
- **ProductDetail.vue (`/product/:id`)**: 商品详情页面。
- **AdminPage.vue (`/admin`)**: 管理员后台页面 (目前为占位)。
- **MyPosts.vue (`/userhome/myposts` -> UserHome 内的组件)**: 用户管理自己发布的商品列表及表单。
- **MessageList.vue (`/messages`)**: 私信会话列表页面。
- **ChatWindow.vue (`/chat`)**: 聊天窗口页面。
- **BrowseProducts.vue (UserHome 内的组件)**: 商品浏览区。
- **Favorites.vue (UserHome 内的组件)**: 用户收藏列表。
- **UserProfile.vue (UserHome 内的组件)**: 用户个人信息编辑。

## 2. 功能点实现状态及与后端 API 符合度

### 2.1 用户认证 (LoginRegister.vue)
- **前端实现**: 提供登录和注册表单，切换逻辑。
- **API 对接**: 
    - **登录**: 调用 `loginApi` (位于 `src/api/user.js`) 对接 `POST /api/auth/login`。成功后正确处理 `AuthResponse`，存储 `token` 和 `userProfileDto`。
    - **注册**: 调用 `registerApi` (位于 `src/api/user.js`) 对接 `POST /api/auth/register`，包含 `username`, `password`, `email`。
- **状态**: **已对接后端 API**。登录成功后会存储用户信息并根据角色跳转。

### 2.2 商品浏览 (BrowseProducts.vue, ProductCard.vue)
- **前端实现**: 商品列表展示，支持关键词搜索。使用 `ProductCard` 组件显示单个商品。
- **API 对接**: `BrowseProducts.vue` 调用 `getAllProductsApi` (位于 `src/api/product.js`) 对接后端 `GET /api/products`。处理加载、错误和空状态。将后端 `ProductResponseDto` 映射到 `ProductCard`。
- **状态**: **已对接后端 API**。

### 2.3 商品详情 (ProductDetail.vue)
- **前端实现**: 展示商品详细信息、图片、价格、描述、卖家、分类、浏览量。提供收藏按钮、私信卖家按钮、评论区(模拟)。
- **API 对接**:
    - **商品数据**: 调用 `getProductByIdApi` (位于 `src/api/product.js`) 对接后端 `GET /api/products/{id}`。
    - **收藏功能**: 调用 `isProductFavoritedApi`, `addFavoriteApi`, `removeFavoriteByProductIdApi` (位于 `src/api/favorite.js`) 对接后端收藏相关API。
    - **评论功能**: **本地模拟提交和展示**。后端 `modules.md` 无评论 API。
    - **私信卖家**: 跳转到聊天页面，聊天功能本身未对接后端。
- **状态**: **商品数据和收藏功能已对接后端 API**。评论功能为模拟。

### 2.4 我的发布 (MyPosts.vue - UserHome 子组件)
- **前端实现**: 列表展示用户发布的商品，提供发布新商品、编辑、删除商品的表单和功能。
- **API 对接**:
    - **获取列表**: 调用 `getProductsByUserIdApi` (位于 `src/api/product.js`) 对接后端 `GET /api/products/user/{userId}`。
    - **发布商品**: 调用 `createProductApi` (位于 `src/api/product.js`) 对接后端 `POST /api/products`。
    - **编辑商品**: 调用 `updateProductApi` (位于 `src/api/product.js`) 对接后端 `PUT /api/products/{id}`。
    - **删除商品**: 调用 `deleteProductApi` (位于 `src/api/product.js`) 对接后端 `DELETE /api/products/{id}`。
    - **图片上传**: 调用 `uploadImageApi` (位于 `src/api/upload.js`) 对接后端 `POST /api/upload/image`，获取图片 URL 后用于商品信息提交。
- **状态**: **已对接后端 API**。需要后端实现图片上传接口 (`/api/upload/image`) 和商品分类选择的进一步完善。

### 2.5 我的收藏 (Favorites.vue - UserHome 子组件)
- **前端实现**: 列表展示用户收藏的商品，提供取消收藏功能。
- **API 对接**:
    - **获取收藏列表**: **使用 `localStorage` 本地模拟**，未调用后端 `GET /api/favorites/my`。
    - **取消收藏**: **本地模拟操作并更新 `localStorage`**，未调用后端 `DELETE /api/favorites/{favoriteId}`。
- **状态**: **已对接后端 API**。使用 `getMyFavoritesApi` 获取列表，`removeFavoriteByFavoriteIdApi` 取消收藏。

### 2.6 个人信息 (UserProfile.vue - UserHome 子组件)
- **前端实现**: 表单展示和修改用户头像、昵称、邮箱、简介，提供修改密码功能。
- **API 对接**:
    - **加载用户信息**: **使用模拟数据**，未调用后端 `GET /api/users/me`。
    - **保存用户信息**: **本地模拟成功提示**，未调用后端 `PUT /api/users/me`。头像上传未对接。
    - **修改密码**: **本地模拟成功提示**。后端 `modules.md` 无修改密码 API。
- **状态**: **已对接后端 API** (获取和更新基本信息、头像上传)。修改密码功能仍为模拟，等待后端API。

### 2.7 管理员后台 (AdminPage.vue)
- **前端实现**: 简单的占位页面。
- **API 对接**: 无。
- **状态**: **未实现具体功能**。需对接后端管理员相关 API (如分类管理 `POST/PUT/DELETE /api/categories`)。

### 2.8 私信/聊天功能 (MessageList.vue, ChatWindow.vue)
- **前端实现**: 私信会话列表页面和聊天窗口界面。
- **API 对接**: 
    - `MessageList.vue`: **使用模拟数据**加载会话列表。
    - `ChatWindow.vue`: **使用模拟数据**加载聊天记录，发送消息为本地模拟。
- **状态**: **完全未对接后端 API，使用模拟数据**。后端 `modules.md` 无对应 API，需后端支持（如获取会话、获取消息、发送消息、WebSocket等）。

## 3. 总结与建议

- **主要问题**: 前端部分核心功能 (用户认证、商品浏览、商品详情、收藏) 已对接后端 API。但其他功能如"我的发布"、"我的收藏列表"、"个人信息管理"以及"管理员"、"聊天"等仍依赖模拟数据或本地操作。
- **下一步需要对接的后端API (根据 modules.md)**:
    - 用户发布的商品 (列表 `GET /api/products/user/{userId}`, 发布 `POST /api/products`, 更新 `PUT /api/products/{id}`, 删除 `DELETE /api/products/{id}`), 包括图片上传。
    - 用户收藏列表 (`GET /api/favorites/my`)。
    - 用户信息获取 (`GET /api/users/me`) 和更新 (`PUT /api/users/me`)，包括头像上传。
    - 商品分类管理 (管理员权限，`GET /api/categories`, `POST /api/categories`, `PUT /api/categories/{id}`, `DELETE /api/categories/{id}`).
- **需要后端补充的API (基于前端设计)**:
    - 商品评论 (获取/提交)。
    - 修改密码 (`PUT /api/users/me/password` 或类似)。
    - 私信/聊天 (获取会话列表, 获取聊天记录, 发送消息, 可能需要WebSocket)。
    - 统一的图片上传接口 (`POST /api/upload/image` 或类似)。
- **Playwright 检查概念**: 部分核心流程已可进行集成测试。其余部分仍受限于模拟数据。

