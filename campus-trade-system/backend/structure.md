# 后端代码架构

## 根目录 (`campus-trade-system/backend`)

- `pom.xml`: Maven 项目对象模型文件，定义项目依赖、插件和构建配置。
- `uploads/`: (推测) 用户上传文件的存储目录。
- `target/`: Maven 构建输出目录，包含编译后的代码和打包文件。
- `src/`: 项目源代码目录。

## `src/main/java/com/example/yourproject/` - Java 核心代码

- **`CampusTradeSystemApplication.java`**: Spring Boot 应用主入口类，负责启动整个应用程序。

- **`config/`**: 配置类目录。
    - `SecurityConfig.java`: Spring Security 配置，处理认证和授权。
    - `WebMvcConfig.java`: Spring MVC 配置，如拦截器、资源处理器等。
    - `StorageProperties.java`: 文件存储相关配置属性。
    - `PasswordEncoderConfig.java`: 密码加密器配置。
    - `JwtRequestFilter.java`: JWT 请求过滤器，用于验证 Token。
    - `OpenApiConfig.java`: OpenAPI (Swagger) 文档配置。
    - `JwtAuthenticationEntryPoint.java`: JWT 认证入口点，处理认证失败的情况。

- **`controller/`**: API 接口控制器层，处理 HTTP 请求并返回响应。
    - `AuthController.java`: 处理用户认证相关的请求，如登录、注册。
    - `CategoryController.java`: 处理商品分类相关的 API 请求。
    - `CommentController.java`: 处理评论相关的 API 请求。
    - `ConversationController.java`: (推测) 处理用户间对话/消息相关的 API 请求。
    - `FavoriteController.java`: 处理收藏相关的 API 请求。
    - `FileUploadController.java`: 处理文件上传的 API 请求。
    - `OrderController.java`: 处理订单相关的 API 请求。
    - `ProductController.java`: 处理商品相关的 API 请求。
    - `UserController.java`: 处理用户个人信息相关的 API 请求。

- **`dto/`**: 数据传输对象 (Data Transfer Objects)，用于在不同层之间传递数据。
    - `ApiResponse.java`: 通用的 API 响应结构。
    - `AuthResponse.java`: 认证响应，如包含 Token。
    - `CategoryCreateRequest.java`, `CategoryDto.java`, `CategoryUpdateRequest.java`: 分类相关的 DTO。
    - `CommentCreateRequest.java`, `CommentDto.java`: 评论相关的 DTO。
    - `ConversationDto.java`, `MessageDto.java`: (推测) 对话/消息相关的 DTO。
    - `FavoriteDto.java`, `FavoriteRequest.java`: 收藏相关的 DTO。
    - `OrderCreateRequest.java`, `OrderResponseDto.java`, `OrderStatusUpdateRequest.java`: 订单相关的 DTO。
    - `ProductBasicDTO.java`, `ProductCreateDTO.java`, `ProductDetailDTO.java`, `ProductResponseDto.java`, `ProductUpdateRequest.java`: 商品相关的 DTO。
    - `UserDTO.java`, `UserLoginRequest.java`, `UserProfileDto.java`, `UserRegisterRequest.java`, `UserUpdateRequest.java`: 用户相关的 DTO。
    - `ChangePasswordRequest.java`: 用户修改密码请求的 DTO。
    - **`deepseek/`**: 与 DeepSeek AI 服务交互相关的 DTO。
        - `DeepSeekChatRequest.java`
        - `DeepSeekChatResponse.java`

- **`exception/`**: 自定义异常类和全局异常处理。
    - `BusinessRuleException.java`: 业务规则异常。
    - `FileStorageException.java`: 文件存储异常。
    - `GlobalExceptionHandler.java`: 全局异常处理器，统一处理应用中的各种异常。
    - `ResourceNotFoundException.java`: 资源未找到异常。
    - `UnauthorizedOperationException.java`: 未授权操作异常。

- **`mapper/`**: MyBatis Mapper 接口，定义数据库操作方法。
    - `CategoryMapper.java`
    - `CommentMapper.java`
    - `FavoriteMapper.java`
    - `OrderMapper.java`
    - `ProductMapper.java`
    - `UserMapper.java`

- **`model/`**: 数据实体模型 (POJO)，对应数据库中的表结构。
    - `Category.java`
    - `Comment.java`
    - `Conversation.java`
    - `Favorite.java`
    - `Message.java`
    - `Order.java`
    - `Product.java`
    - `User.java`
    - **`enums/`**: 枚举类型。
        - `OrderStatus.java`
        - `ProductStatus.java`

- **`service/`**: 业务逻辑服务接口。
    - `CategoryService.java`
    - `CommentService.java`
    - `ConversationService.java`
    - `DeepSeekService.java`: (推测) 与 DeepSeek AI 服务交互的业务逻辑。
    - `FavoriteService.java`
    - `FileStorageService.java`: 文件存储服务接口。
    - `FileUploadService.java`: 文件上传服务 (似乎与 `FileStorageService` 有重叠或协作关系)。
    - `OrderService.java`
    - `ProductService.java`
    - `UserService.java`
    - **`impl/`**: 业务逻辑服务接口的实现类。
        - `CategoryServiceImpl.java`
        - `CommentServiceImpl.java`
        - `ConversationServiceImpl.java`
        - `DeepSeekServiceImpl.java`
        - `FavoriteServiceImpl.java`
        - `FileSystemStorageService.java`: 文件系统存储服务的具体实现。
        - `OrderServiceImpl.java`
        - `ProductServiceImpl.java`
        - `UserServiceImpl.java`
        - `changePassword()`: 实现了修改用户密码的逻辑，包括验证旧密码和加密新密码。

- **`util/`** (或 `utils/`): 工具类。
    - `JwtTokenUtil.java`: JWT Token 生成和验证工具类。
    - `Result.java`: (可能在 `utils/`) 通用的结果封装类。
    - `ProductController.java`: 处理商品相关的 API 请求。
    - `UserController.java`: 处理用户个人信息相关的 API 请求。
        - `POST /api/users/me/change-password`: 添加了修改当前登录用户密码的端点。

## `src/main/resources/` - 资源文件

- `application.properties`: Spring Boot 应用程序的主要配置文件，如数据库连接、服务器端口等。
- `schema.sql`: (推测) 数据库表结构初始化脚本。
- `data.sql`: (推测) 数据库初始数据填充脚本。
- **`mappers/`**: MyBatis Mapper XML 文件，包含具体的 SQL 语句。
    - `CategoryMapper.xml`
    - `CommentMapper.xml`
    - `FavoriteMapper.xml`
    - `OrderMapper.xml`
    - `ProductMapper.xml`
    - `UserMapper.xml`
- **`db/`**: 其他数据库相关脚本。
    - `update_users_table.sql`: (推测) 更新用户表的特定脚本。

## 实时通信

- `ConversationController`: 管理用户间会话，提供创建会话、获取会话列表等功能
- `MessageDto`: 定义消息数据结构，包括发送者、内容、时间戳等信息
- 支持消息的已读状态管理
- `ConversationServiceImpl`: 原先提供内存模拟实现，现已升级为与数据库交互的完整实现
  - 通过MyBatis Mapper将对话和消息持久化到数据库
  - 实现参与者关系验证，确保消息安全性
  - 自动维护会话元数据，如最后消息时间和未读计数
  - 完整的错误处理和调试日志机制

## 数据访问层增强

- `ConversationMapper`: 提供会话数据的CRUD操作
  - 基于MyBatis注解的SQL查询
  - 按用户查找会话、按会话ID查找、更新会话等功能
  - 优化`findByUsers`查询：移除了可能与特定数据库（如达梦）不兼容的`LIMIT 1`子句，以增强查询的健壮性。如果确实需要限制结果数量且数据库支持，应考虑使用数据库特定的分页语法或在业务逻辑层面处理。
  - 为 `findByUsers` 方法的参数添加了 `@Param` 注解，以解决 MyBatis 参数绑定时 `BindingException: Parameter 'user1Id' not found` 的问题。
  - 为 `updateLastMessageTime` 方法的参数添加了 `@Param` 注解，以解决 MyBatis 参数绑定问题，避免因此导致的事务回滚。
  - 添加诊断方法 `findAllByUsersForCleanup` 用于在出现 `TooManyResultsException` 时辅助排查重复数据。
- `MessageMapper`: 提供消息数据的CRUD操作
  - 为 `findByConversationId`, `countUnreadByConversationAndReceiverId`, `markAllAsRead` 等多参数方法添加了 `@Param` 注解，以解决 MyBatis 参数绑定时 `BindingException: Parameter not found` 的问题。
  - 分页查询会话消息
  - 统计未读消息数量
  - 标记消息为已读状态
- 使用事务注解确保数据一致性

## 服务层 (`service/impl`)
- `ConversationServiceImpl`:
  - 在 `createOrGetConversation` 方法中实现用户ID规范化（如确保 `user1Id < user2Id`），以维护数据一致性。
  - 增强了对数据库操作（如 `findByUsers`, `insert`）的异常捕获，增加了详细日志记录，特别是针对 `TooManyResultsException` 和其他潜在的数据库异常，以便更精确地诊断问题。
  - 增强了 `convertToDto` 方法的健壮性：添加了全面的空值检查（针对 `conversation`, `currentUserId`, `otherUserId`, `userService`, `messageMapper`），并在调用依赖服务获取用户信息和未读消息数时使用详细的 `try-catch` 块和日志记录，确保打印完整异常堆栈，以帮助诊断 `message: null` 类型的错误。
  - 增强了 `convertToDto(Conversation conversation, ...)` 方法的健壮性：添加了全面的空值检查，并在调用依赖服务获取用户信息和未读消息数时使用详细的 `try-catch` 块和日志记录，确保打印完整异常堆栈。
  - 增强了 `convertToDto(Message message)` 方法的健壮性：添加了全面的空值检查（针对 `message`, `userService`），并在调用 `userService` 获取发送者和接收者详细信息时使用详细的 `try-catch` 块和日志记录，确保打印完整异常堆栈，以帮助诊断因 DTO 转换失败可能导致的事务回滚问题。

## 用户身份识别架构

系统采用了多层次的用户身份识别与解析策略：

1. **主要身份识别流程**：
   - 使用Spring Security的`SecurityContextHolder`获取当前认证用户
   - 支持`UsernamePasswordAuthenticationToken`和其它认证令牌类型
   - 从`UserDetails`或`Authentication.getName()`中提取身份标识

2. **身份解析增强**：
   - 支持多种形式的用户标识，包括数字ID和非数字用户名
   - 提供从用户名到用户ID的解析能力，不再仅依赖于数字格式
   - 使用数据库查询实现用户名到ID的映射关系

3. **错误恢复机制**：
   - 在无法获取用户ID时提供详细的日志记录
   - 实现多层次的回退策略，确保关键操作继续执行
   - 与现有安全架构保持兼容性，避免破坏其它功能

4. **调试支持**：
   - 全面的认证过程日志记录，帮助识别身份问题
   - 详细记录身份解析过程中的各个步骤
   - 通过配置化的默认值支持开发和测试场景

这种多层次的身份识别架构提高了系统的健壮性，使应用能够在各种认证场景下正确识别用户身份，特别是支持了非数字格式的用户名，同时保持了与现有安全模型的兼容性。

## 总结

该后端项目是一个基于 Spring Boot 和 Maven 构建的 Java Web 应用程序。其架构遵循了典型的分层设计模式：

- **Controller 层**: 负责接收和响应 HTTP 请求，是用户与应用交互的入口。
- **Service 层**: 实现核心业务逻辑，处理数据和执行操作。
- **Mapper/DAO 层**: (通过 MyBatis 实现) 负责与数据库进行交互，执行 CRUD 操作。
- **Model 层**: 定义数据实体，映射数据库表。
- **DTO 层**: 用于在各层之间高效、安全地传输数据。
- **Config 层**: 管理应用的各种配置，如安全、MVC、数据库等。
- **Exception Handling**: 统一处理应用中可能发生的异常。
- **Utils/Util**: 提供通用的工具函数。

项目还集成了以下技术/功能：
- **Spring Security 和 JWT**: 用于用户认证和授权。
  - 使用`SecurityContextHolder`获取当前登录用户信息
  - 通过`JwtRequestFilter`进行Token验证
  - 支持用户会话的创建和管理
- **MyBatis**: 作为持久层框架，简化数据库操作。
- **文件上传与存储**: 提供了文件上传和管理的能力。
- **OpenAPI (Swagger)**: 用于生成和展示 API 文档。
- **DeepSeek AI**: (推测) 集成了 DeepSeek AI 服务，可能用于智能问答、内容生成等功能。
- **实时通信**: 通过会话和消息功能实现用户间的即时通信
  - `ConversationController`: 管理用户间会话，提供创建会话、获取会话列表等功能
  - `MessageDto`: 定义消息数据结构，包括发送者、内容、时间戳等信息
  - 支持消息的已读状态管理
  - `ConversationServiceImpl`: 原先提供内存模拟实现，现已升级为与数据库交互的完整实现
    - 通过MyBatis Mapper将对话和消息持久化到数据库
    - 实现参与者关系验证，确保消息安全性
    - 自动维护会话元数据，如最后消息时间和未读计数
    - 完整的错误处理和调试日志机制

- **数据访问层增强**:
  - `ConversationMapper`: 提供会话数据的CRUD操作
    - 基于MyBatis注解的SQL查询
    - 按用户查找会话、按会话ID查找、更新会话等功能
  - `MessageMapper`: 提供消息数据的CRUD操作
    - 分页查询会话消息
    - 统计未读消息数量
    - 标记消息为已读状态
  - 使用事务注解确保数据一致性

该结构清晰，模块化程度较高，便于维护和扩展。

## 数据库初始化机制

系统采用了多层次的数据库初始化策略：

 **实体类映射优化**：
   - 所有实体类都使用JPA注解，确保与数据库表结构一致
   - `@Entity`、`@Table`、`