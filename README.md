校园二手交易系统1. 项目标题：校园二手交易系统2. 引言系统概述与目的本项目旨在构建一个“校园二手交易系统”，作为学生应用系统架构课程的实践项目。其核心目标是为校园内的学生提供一个便捷的平台，用于买卖二手物品。作为一个学习项目，系统将在满足核心功能的前提下，控制不必要的复杂度，同时着重于关键架构原则的实践与展示。关键技术概览系统将采用以下主要技术栈：后端使用 Java 和 Spring Boot 框架，前端采用 Vue3，数据持久化选用 MyBatis 操作达梦（DM）数据库，并集成 Redis 进行缓存优化。此外，系统还将接入外部服务，如用于智能文本处理的 Deepseek API、提供实时天气信息的 Web 服务，以及一个基于AI技术的智能体编排（以阿里云为例，进行简化实现）。3. 核心需求概要本项目严格遵循以下技术与功能要求：技术栈规定
开发语言： Java
数据库： 达梦数据库 (DM) (此为硬性要求)
主要开发架构： Spring MVC
核心开发技术： Vue3, Spring Boot
数据库操作层： MyBatis
接口技术： Deepseek API, Redis, Web Service (例如：天气预报)
AI技术： 基于AI技术设计和开发智能体编排功能 (例如：阿里云平台，进行简化实现)
功能范围
系统需包含 4-6个 明确的功能模块。
必须包含至少一个涉及 主从表结构（带有业务外键）的业务功能。
4. 系统架构高层架构图为了清晰地展示系统各组成部分及其交互方式，一个高层架构图是必不可少的。此图应能帮助理解整个系统的工作流程。（建议此处实际绘制或使用工具生成一个架构图，以下为文字描述的替代方案，说明了核心组件及其交互关系）+---------------------+      +---------------------------------+      +-------------------+
| Vue3 前端 |----->| Spring Boot 后端 (业务逻辑/API) |----->| MyBatis (数据访问)|
| (用户界面) |<-----| |<-----| |
+---------------------+      +---------------------------------+      +-------------------+
| ^ |
| | v
                                      v | +-------------------+
                              +-------------+ | | 达梦数据库 (DM) |
| Redis (缓存)| | +-------------------+
                              +-------------+ |
                                      ^ |
| |
      +----------------------------------------------------------------------------------+
| 外部服务 (Deepseek API, 天气API, AI Agent API) |
      +----------------------------------------------------------------------------------+
此图展示了用户通过Vue3前端与系统交互，请求发送至Spring Boot后端。后端处理业务逻辑，通过MyBatis与达梦数据库进行数据交换，利用Redis进行数据缓存，并与外部API（如Deepseek、天气服务、AI智能体）通信以提供增强功能。这种分层架构有助于模块解耦和独立开发。组件描述
前端 (Vue3)： 负责用户交互、数据展示以及向后端发送请求。用户通过浏览器访问前端应用，完成商品浏览、发布、竞拍等操作。
后端 (Spring Boot/Spring MVC)： 核心业务逻辑处理中心。它通过RESTful API向前端提供服务，管理数据库交互，并集成各类外部服务。
数据库 (达梦 DM)： 系统的持久化存储层，负责存储所有应用数据，如用户信息、商品信息、出价记录等。
数据访问层 (MyBatis)： 作为对象关系映射（ORM）框架，简化了Java对象与数据库记录之间的映射，使SQL交互更为便捷 1。它允许开发者更灵活地控制SQL语句。
缓存 (Redis)： 用于存储频繁访问的数据，如热门商品、天气信息等，以减少数据库负载，提升系统响应速度和性能 3。
外部API： 提供专业化的功能，例如通过Deepseek API进行智能文本处理（如商品描述生成），通过天气API获取实时天气数据。
推荐项目结构 (Maven多模块)为了更好地组织代码、分离关注点，并支持未来可能的独立开发和部署，推荐采用Maven多模块项目结构。这种结构在许多实际项目中被广泛应用，有助于保持代码的清晰性和可维护性，尤其对于包含前端和后端两大部分的系统而言 5。campus-trade-system/
├── pom.xml             # 父POM，管理整个项目的依赖和插件
├── backend/            # 后端Spring Boot应用模块
│   ├── pom.xml         # 后端模块的POM
│   └── src/            # 后端Java源代码和资源文件
└── frontend/           # 前端Vue3应用模块
    ├── pom.xml         # 前端模块的POM (可选, 主要用于通过Maven触发npm构建)
    └── src/            # 前端Vue源代码和资源文件
这种结构将后端和前端代码明确分开，使得团队成员可以并行工作，也方便对各模块进行独立的构建和测试。5. 技术栈详情为了确保开发过程的顺利进行和各组件间的兼容性，下表列出了项目中使用的主要技术及其推荐版本。选择稳定且社区支持良好的版本，有助于减少开发过程中可能遇到的兼容性问题。
技术类别推荐版本备注Java开发语言JDK 17 (或根据课程要求调整)确保与Spring Boot版本兼容Spring Boot后端框架3.x.x选用较新的稳定版本Vue3前端框架3.x.x主流前端框架MyBatisORM框架3.5.x灵活的SQL映射框架MyBatis Spring Boot Starter集成库3.0.x 12简化MyBatis与Spring Boot的集成达梦JDBC驱动数据库驱动DmJdbcDriver18, 版本 8.1.x.x (如8.1.3.140 7)需确认与所用达梦数据库版本兼容，可从Maven中央仓库获取 7Spring Data Redis缓存集成(与Spring Boot版本兼容)提供Redis操作的便捷APIJedis/LettuceRedis客户端(由Spring Data Redis引入)Spring Data Redis底层依赖的客户端Maven构建工具3.6+项目构建和依赖管理Node.js前端运行时LTS 版本 (如 18.x 或 20.x 5)执行Vue3前端构建和开发服务器npm/yarn前端包管理器(随Node.js安装)管理前端项目依赖
6. 环境搭建与配置前置条件
JDK： 安装指定版本 (如 JDK 17)。
Maven： 安装指定版本 (如 3.6+)。
Node.js 和 npm/yarn： 安装指定版本 (如 Node.js LTS 5)。
达梦数据库 (DM)：

根据课程要求或官方文档安装并配置达梦数据库。鉴于用户对达梦数据库不太熟悉，务必仔细阅读其安装和配置指南。
关键步骤：为本项目创建一个专用的数据库实例或模式（Schema）。记录下数据库的连接URL、用户名和密码，后续配置会用到。
确保达梦数据库服务已启动并可通过网络访问。


后端 (Spring Boot) 配置pom.xml 关键依赖在后端模块的 pom.xml 文件中，需要添加以下核心依赖：XML<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>3.0.3</version> </dependency>

    <dependency>
        <groupId>com.dameng</groupId>
        <artifactId>DmJdbcDriver18</artifactId> <version>8.1.3.140</version> </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
确保添加了正确的达梦JDBC驱动依赖。DmJdbcDriver18 及其版本号 8.1.3.140 是基于现有信息的示例 7，实际使用时应核对与您的达梦数据库版本最匹配的驱动。application.properties (或 application.yml)在 src/main/resources 目录下创建 application.properties 文件，并配置以下属性：Properties# 服务器配置
server.port=8080

# Spring应用配置
spring.application.name=campus-second-hand-trading

# 达梦数据库 (DM) 配置
# 请替换为您的实际数据库名称、用户名和密码
# 达梦数据库的默认端口通常是 5236
spring.datasource.url=jdbc:dm://localhost:5236/YOUR_DB_NAME # 例如：jdbc:dm://127.0.0.1:5236/TRADE_DB [8]
spring.datasource.username=YOUR_DM_USERNAME
spring.datasource.password=YOUR_DM_PASSWORD
spring.datasource.driver-class-name=dm.jdbc.driver.DmDriver # 达梦JDBC驱动类名 [8]
# 如果需要，可以添加达梦特定的连接属性，例如：
# spring.datasource.hikari.connection-init-sql=SET CASE_SENSITIVE_LOGON FALSE

# MyBatis 配置
# 指定MyBatis Mapper XML文件的位置 [1]
mybatis.mapper-locations=classpath:mappers/*.xml
# 开启驼峰命名转换：数据库下划线命名自动映射到Java驼峰命名属性
mybatis.configuration.map-underscore-to-camel-case=true
# 指定实体类别名扫描包，方便在Mapper XML中直接使用类名
# mybatis.type-aliases-package=com.example.yourproject.model # 请根据您的项目包结构调整

# Redis 配置
spring.data.redis.host=localhost
spring.data.redis.port=6379
# 如果Redis设置了密码，取消注释并配置:
# spring.data.redis.password=YOUR_REDIS_PASSWORD

# 外部API密钥 (重要：生产环境中应使用更安全的方式管理密钥，如环境变量或配置中心)
deepseek.api.key=YOUR_DEEPSEEK_API_KEY
weather.api.key=YOUR_WEATHER_API_KEY
alibaba.agent.api.key=YOUR_ALIBABA_AGENT_API_KEY
alibaba.agent.api.endpoint=YOUR_ALIBABA_AGENT_ENDPOINT
此配置文件是Spring Boot应用的核心，正确配置数据库连接信息至关重要。对于达梦数据库，driver-class-name 和 url 格式务必准确 8。API密钥的管理，在开发阶段可以直接写入配置文件，但在生产环境中，强烈建议采用环境变量、Vault或Spring Cloud Config等更安全的方式。前端 (Vue3) 配置
进入 frontend 目录。
执行 npm install (或 yarn install) 下载前端项目所需的依赖包。
配置API基础URL：在Vue3项目中，通常会有一个配置文件（如 .env.development, .env.production 或一个专门的 src/config/api.js 文件）来指定后端API的地址。将其指向Spring Boot后端服务，例如 VUE_APP_API_BASE_URL=http://localhost:8080/api。
达梦数据库 Schema 设计以下是核心实体表的 CREATE TABLE SQL语句，请使用达梦数据库管理工具执行。建表语句已考虑到达梦数据库的数据类型兼容性 9。USERS (用户表)存储用户信息，用于认证和授权。SQLCREATE TABLE USERS (
    user_id INT IDENTITY(1,1) PRIMARY KEY,    -- 用户ID，自增主键 (DM支持IDENTITY)
    username VARCHAR(50) NOT NULL UNIQUE,     -- 用户名，唯一且不能为空
    password_hash VARCHAR(255) NOT NULL,      -- 存储哈希后的密码，确保安全
    email VARCHAR(100) NOT NULL UNIQUE,       -- 电子邮箱，唯一且不能为空
    registration_date TIMESTAMP DEFAULT SYSTIMESTAMP, -- 注册日期，默认为当前时间 (DM支持SYSTIMESTAMP)
    last_login_date TIMESTAMP                 -- 最后登录日期
);
-- 常用达梦数据类型: INT, VARCHAR, TIMESTAMP [9, 10]
CATEGORIES (商品分类表)用于组织和管理商品分类。SQLCREATE TABLE CATEGORIES (
    category_id INT IDENTITY(1,1) PRIMARY KEY, -- 分类ID，自增主键
    category_name VARCHAR(100) NOT NULL UNIQUE, -- 分类名称，唯一且不能为空
    description CLOB                            -- 分类描述，使用CLOB存储较长文本 [9]
);
PRODUCTS (商品表 - 主表)存储二手商品的详细信息，这是交易的核心。SQLCREATE TABLE PRODUCTS (
    product_id INT IDENTITY(1,1) PRIMARY KEY,  -- 商品ID，自增主键
    user_id INT NOT NULL,                     -- 发布者ID (外键关联USERS表)
    category_id INT NOT NULL,                 -- 商品分类ID (外键关联CATEGORIES表)
    title VARCHAR(200) NOT NULL,              -- 商品标题
    description CLOB,                         -- 商品描述，使用CLOB存储详细描述
    price DECIMAL(10,2) NOT NULL,             -- 商品价格，使用DECIMAL保证精度 [9]
    status VARCHAR(20) DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'PENDING_PURCHASE', 'SOLD')), -- 商品状态，调整状态以适应购买流程
    post_date TIMESTAMP DEFAULT SYSTIMESTAMP,   -- 发布日期
    views INT DEFAULT 0,                      -- 浏览次数
    CONSTRAINT FK_PRODUCT_USER FOREIGN KEY (user_id) REFERENCES USERS(user_id),
    CONSTRAINT FK_PRODUCT_CATEGORY FOREIGN KEY (category_id) REFERENCES CATEGORIES(category_id)
);
-- 达梦数据库的DDL外键语法与标准SQL类似 [11, 12]。
-- DECIMAL 和 CLOB 是达梦支持的合适数据类型 [9, 10]。

ORDERS (订单表 - 从表)
此表用于记录用户购买商品的订单信息，并满足“主从表结构，带业务外键”的要求。PRODUCTS 表是主表，ORDERS 表是从表，通过 product_id 业务外键关联。SQLCREATE TABLE ORDERS (
    order_id INT IDENTITY(1,1) PRIMARY KEY,       -- 订单ID，自增主键
    product_id INT NOT NULL,                  -- 关联的商品ID (外键)
    buyer_user_id INT NOT NULL,               -- 买家用户ID (外键关联USERS表)
    seller_user_id INT NOT NULL,              -- 卖家用户ID (对应商品发布者ID)
    order_price DECIMAL(10,2) NOT NULL,         -- 订单成交价格 (商品购买时价格)
    order_status VARCHAR(30) DEFAULT 'PENDING_CONFIRMATION' CHECK (order_status IN ('PENDING_CONFIRMATION', 'CONFIRMED_BY_SELLER', 'AWAITING_PAYMENT', 'PAID', 'SHIPPED', 'COMPLETED', 'CANCELLED_BY_BUYER', 'CANCELLED_BY_SELLER', 'REFUND_PENDING', 'REFUNDED')), -- 订单状态
    created_at TIMESTAMP DEFAULT SYSTIMESTAMP,    -- 下单时间
    updated_at TIMESTAMP DEFAULT SYSTIMESTAMP,    -- 订单最后更新时间
    CONSTRAINT FK_ORDER_PRODUCT FOREIGN KEY (product_id) REFERENCES PRODUCTS(product_id),
    CONSTRAINT FK_ORDER_BUYER FOREIGN KEY (buyer_user_id) REFERENCES USERS(user_id),
    CONSTRAINT FK_ORDER_SELLER FOREIGN KEY (seller_user_id) REFERENCES USERS(user_id)
);
-- ORDERS表通过product_id字段与PRODUCTS表建立了明确的主从关系。
-- 注意：当商品被下单后，PRODUCTS表中的status应更新为 PENDING_PURCHASE，交易完成后更新为 SOLD。

FAVORITES (收藏表)
用于存储用户收藏的商品信息。SQLCREATE TABLE FAVORITES (
    favorite_id INT IDENTITY(1,1) PRIMARY KEY,  -- 收藏ID，自增主键
    user_id INT NOT NULL,                       -- 用户ID (外键关联USERS表)
    product_id INT NOT NULL,                    -- 商品ID (外键关联PRODUCTS表)
    created_at TIMESTAMP DEFAULT SYSTIMESTAMP,    -- 收藏时间
    CONSTRAINT FK_FAVORITE_USER FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE,
    CONSTRAINT FK_FAVORITE_PRODUCT FOREIGN KEY (product_id) REFERENCES PRODUCTS(product_id) ON DELETE CASCADE,
    CONSTRAINT UQ_USER_PRODUCT_FAVORITE UNIQUE (user_id, product_id) -- 确保一个用户对一个商品只能收藏一次
);

这些表结构构成了二手交易系统的基础。USERS 表管理用户，CATEGORIES 表对商品进行分类，PRODUCTS 表存储商品信息，ORDERS 表记录交易订单，FAVORITES 表管理用户收藏。
7. 功能模块 (4-6个示例)以下是建议的系统功能模块，涵盖了核心需求并利用了指定的技术。

用户认证与管理

描述： 提供用户注册、登录、登出功能，以及用户查看和更新个人资料的接口。
相关表： USERS
关键API端点：

POST /api/auth/register：用户注册，提交用户名、密码、邮箱等信息。
POST /api/auth/login：用户登录，验证凭据并发放token。
POST /api/auth/logout：用户登出，使token失效。
GET /api/users/me：获取当前登录用户的个人资料。
PUT /api/users/me：更新当前登录用户的个人资料。





商品浏览与搜索

描述： 用户可以浏览所有发布的二手商品，按分类筛选，或通过关键词搜索商品。
相关表： PRODUCTS, CATEGORIES, USERS (用于展示卖家信息)。
关键API端点：

GET /api/products：获取商品列表，支持分页、按分类/关键词过滤。
GET /api/products/{productId}：获取单个商品的详细信息。
GET /api/categories：获取所有商品分类列表（此接口适合使用Redis缓存 3）。





发布新商品

描述： 认证用户可以将自己的二手物品发布到平台上进行售卖。
相关表： PRODUCTS
关键API端点：

POST /api/products：创建新商品，需要用户认证。请求体包含商品标题、描述、价格、分类等信息。





商品交易与订单管理 (演示主从表功能)

描述： 用户可以购买平台上的二手商品。卖家可以管理其售出商品的订单。此功能直接体现了 PRODUCTS (主表) 和 ORDERS (从表) 之间的关联。
相关表： PRODUCTS (主表), ORDERS (从表), USERS。
关键API端点：

POST /api/orders：用户创建订单以购买商品 (需要用户认证)。请求体包含product_id, buyer_user_id, seller_user_id, order_price等。
GET /api/users/me/orders：查看当前用户的所有订单 (买家视角)。
GET /api/users/me/sales：查看当前用户售出的所有订单 (卖家视角，基于ORDERS表中seller_user_id)。
PUT /api/orders/{orderId}/status：更新订单状态 (如卖家确认并发货，买家确认收货等，需要相应权限)。
GET /api/products/{productId}/orders：获取特定商品的所有订单信息 (可选，主要用于卖家查看)。





商品收藏

描述： 用户可以将感兴趣的商品加入自己的收藏列表，方便后续查找和购买。
相关表： FAVORITES, USERS, PRODUCTS。
关键API端点：

POST /api/users/me/favorites/{productId}：添加指定商品到当前用户的收藏列表。
DELETE /api/users/me/favorites/{productId}：从当前用户的收藏列表中移除指定商品。
GET /api/users/me/favorites：获取当前用户收藏的商品列表。



智能商品助手 (集成Deepseek API与AI智能体)

描述： 用户在发布新商品时，系统可利用Deepseek API自动生成商品描述草稿，或根据商品标题和初步描述推荐相关标签/分类。此功能将调用Deepseek API，并通过后端服务模拟一个简单的“AI智能体”调用流程。
相关表： PRODUCTS (主要更新其 description 字段或新增标签字段)。
外部服务： Deepseek API, (简化的) 阿里云AI智能体服务。
关键API端点：

可以集成到 POST /api/products 流程中，在商品保存前调用。
或提供一个独立的辅助接口：POST /api/ai/generate-description?title={productTitle}&initialDesc={initialDescription}


此功能满足了使用Deepseek API 13 和AI智能体编排的要求。通过后端服务协调对Deepseek API的调用（可能包括对结果的初步处理或格式化），可以视为一种简化的“智能体”工作模式。



校园天气展示 (集成Web Service)

描述： 在系统仪表盘或特定页面显示当前校园区域的天气信息。
外部服务： 天气Web Service (例如 Tomorrow.io 15)。
关键API端点：

GET /api/weather/campus：后端从此接口获取外部天气API数据，并可以利用Redis进行缓存 3，以减少对外部API的请求频率并提高响应速度。


此功能满足了集成外部Web服务的要求，并能结合Redis缓存技术进行优化。


8. 后端开发 (Spring Boot)8.1. 数据库层 (MyBatis & 达梦)数据持久化层是系统的基石，MyBatis提供了灵活的SQL控制能力。

Mapper接口： 定义Java接口，其方法对应SQL操作。使用 @Mapper 注解标记接口以便Spring Boot扫描，并使用 @Select, @Insert, @Update, @Delete 等注解定义简单的SQL语句 1。
Java// 文件路径: src/main/java/com/example/yourproject/mapper/ProductMapper.java
package com.example.yourproject.mapper;

import com.example.yourproject.model.Product;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ProductMapper {

    @Select("SELECT * FROM PRODUCTS WHERE product_id = #{productId}")
    Product findById(@Param("productId") int productId);

    @Insert("INSERT INTO PRODUCTS (user_id, category_id, title, description, price, status, post_date) " +
            "VALUES (#{userId}, #{categoryId}, #{title}, #{description}, #{price}, #{status}, SYSTIMESTAMP)")
    @Options(useGeneratedKeys = true, keyProperty = "productId", keyColumn="product_id") // 确保返回自增主键
    int insert(Product product);

    @Update("UPDATE PRODUCTS SET title = #{title}, description = #{description}, price = #{price}, " +
            "category_id = #{categoryId}, status = #{status} WHERE product_id = #{productId}")
    int update(Product product);

    @Delete("DELETE FROM PRODUCTS WHERE product_id = #{productId}")
    int deleteById(@Param("productId") int productId);

    @Select("SELECT * FROM PRODUCTS ORDER BY post_date DESC") // 示例：获取所有商品
    List<Product> findAll();

    // 用于获取商品及其所有出价记录 (主从查询)，具体实现在XML中定义
    Product findProductWithBids(@Param("productId") int productId);
}



XML Mappers (mappers/*.xml)： 对于复杂的SQL查询，尤其是涉及多表连接、动态SQL或主从表数据映射（如一对多集合映射）时，使用XML文件更为合适和强大 1。
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.yourproject.mapper.ProductMapper">
    <!-- 移除原有的 ProductWithBidsResultMap 和 findProductWithBids 查询 -->
    <!-- 可以根据需要添加新的ResultMap和查询，例如获取商品及其关联的订单(如果需要在一个查询中完成) -->
    <!-- 但更常见的是分别查询商品和订单，或者在Service层组装 -->

    <resultMap id="ProductResultMap" type="com.example.yourproject.model.Product">
        <id property="productId" column="product_id"/>
        <result property="userId" column="user_id"/>
        <result property="categoryId" column="category_id"/>
        <result property="title" column="title"/>
        <result property="description" column="description"/>
        <result property="price" column="price"/>
        <result property="status" column="status"/>
        <result property="postDate" column="post_date"/>
        <result property="views" column="views"/>
        <!-- 可以关联查询用户信息 -->
        <association property="seller" javaType="com.example.yourproject.model.User" columnPrefix="u_">
            <id property="userId" column="user_id"/>
            <result property="username" column="username"/>
            <result property="email" column="email"/>
        </association>
        <!-- 如果需要商品关联的分类信息 -->
        <association property="category" javaType="com.example.yourproject.model.Category" columnPrefix="c_">
            <id property="categoryId" column="category_id"/>
            <result property="categoryName" column="category_name"/>
        </association>
    </resultMap>

    <select id="findProductWithSellerAndCategoryById" resultMap="ProductResultMap">
        SELECT
            p.product_id, p.user_id, p.category_id, p.title, p.description, p.price, p.status, p.post_date, p.views,
            u.user_id AS u_user_id, u.username AS u_username, u.email AS u_email,
            c.category_id AS c_category_id, c.category_name AS c_category_name
        FROM
            PRODUCTS p
        JOIN
            USERS u ON p.user_id = u.user_id
        JOIN
            CATEGORIES c ON p.category_id = c.category_id
        WHERE
            p.product_id = #{productId}
    </select>

    <!-- 其他 ProductMapper 的 CRUD 操作... -->
    <!-- ProductMapper.java 中 findById 方法应对应此查询或 findProductWithSellerAndCategoryById -->
    <select id="findById" resultMap="ProductResultMap"> <!-- 保持原有的findById，或者决定是否用上面的 JOIN 查询替代 -->
        SELECT * FROM PRODUCTS WHERE product_id = #{productId}
    </select>

    <insert id="insert" useGeneratedKeys="true" keyProperty="productId" keyColumn="product_id">
        INSERT INTO PRODUCTS (user_id, category_id, title, description, price, status, post_date)
        VALUES (#{userId}, #{categoryId}, #{title}, #{description}, #{price}, #{status}, SYSTIMESTAMP)
    </insert>

    <update id="update">
        UPDATE PRODUCTS SET title = #{title}, description = #{description}, price = #{price},
        category_id = #{categoryId}, status = #{status} WHERE product_id = #{productId}
    </update>

    <delete id="deleteById">
        DELETE FROM PRODUCTS WHERE product_id = #{productId}
    </delete>

    <select id="findAll" resultMap="ProductResultMap"> <!-- 示例：获取所有商品，并关联卖家用户名 -->
        SELECT p.*, u.username as u_username <!-- 简化了，如果用ProductResultMap，需要u_user_id等 -->
        FROM PRODUCTS p
        JOIN USERS u ON p.user_id = u.user_id
        ORDER BY p.post_date DESC
    </select>
    <!-- 建议 findAll 也使用完整的 ProductResultMap 以保持一致性，并 JOIN USERS 和 CATEGORIES -->
    <select id="findAllDetailed" resultMap="ProductResultMap">
        SELECT
            p.product_id, p.user_id, p.category_id, p.title, p.description, p.price, p.status, p.post_date, p.views,
            u.user_id AS u_user_id, u.username AS u_username, u.email AS u_email,
            c.category_id AS c_category_id, c.category_name AS c_category_name
        FROM
            PRODUCTS p
        JOIN
            USERS u ON p.user_id = u.user_id
        JOIN
            CATEGORIES c ON p.category_id = c.category_id
        ORDER BY
            p.post_date DESC
    </select>

</mapper>

这个XML映射文件现在专注于 `Product` 实体本身，以及其关联的卖家(User)和分类(Category)信息。订单相关的查询和映射将在 `OrderMapper.xml` (如果创建)中处理。主从关系（如商品及其订单）通常通过在Service层分别查询然后组装，或者通过专门的查询实现，以避免N+1问题。


Service层： 封装业务逻辑，并使用 @Transactional 注解管理事务，确保数据操作的一致性。Service层调用Mapper接口与数据库交互。

8.2. API端点 (Spring MVC / REST Controllers)使用 @RestController 定义控制器类，通过 @GetMapping, @PostMapping, @PutMapping, @DeleteMapping 等注解映射HTTP请求到具体的处理方法 1。为了规范请求和响应的数据结构，并进行有效的数据校验，推荐使用数据传输对象（DTOs），并结合 @Valid 注解进行校验。Java// 文件路径: src/main/java/com/example/yourproject/controller/ProductController.java
package com.example.yourproject.controller;

import com.example.yourproject.model.Product; // 假设这是您的实体类
import com.example.yourproject.dto.ProductCreateDTO; // 假设这是用于创建商品的DTO
import com.example.yourproject.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// import javax.validation.Valid; // 如果使用 Jakarta EE 9+ (Spring Boot 3+), 使用 jakarta.validation.Valid

import jakarta.validation.Valid; // For Spring Boot 3+
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable int productId) {
        // Product product = productService.getProductWithBids(productId); // 旧的调用
        Product product = productService.getProductById(productId); // 修改为调用普通获取商品信息的方法
        // 注意：getProductById 在Service层可能需要调整为调用类似 ProductMapper 中的 findProductWithSellerAndCategoryById
        if (product!= null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductCreateDTO productCreateDTO) {
        // 实际应用中，ProductCreateDTO 会转换为 Product 实体
        // Product productToCreate = convertDtoToEntity(productCreateDTO);
        // 并且需要设置当前登录用户为发布者
        // productToCreate.setUserId(getCurrentUserId());
        // Product createdProduct = productService.createProduct(productToCreate);
        // return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        // 此处为简化示例
        Product mockProduct = new Product(); // 假设已转换并设置用户ID
        mockProduct.setTitle(productCreateDTO.getTitle());
        //... 其他属性设置
        Product savedProduct = productService.createProduct(mockProduct); // 假设service方法处理了转换和保存
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // 其他更新、删除等端点...
}
8.3. 外部API集成通用方法推荐使用Spring框架提供的 RestClient 来调用外部REST服务，这在Spring Boot 3.x中是推荐的阻塞式HTTP客户端 16。将API调用逻辑封装在专门的Service类中。Java// 文件路径: src/main/java/com/example/yourproject/service/ExternalWeatherService.java
package com.example.yourproject.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.cache.annotation.Cacheable; // 用于缓存

@Service
public class ExternalWeatherService {

    private final RestClient restClient;
    private final String apiKey;
    private final String weatherApiBaseUrl = "https://api.tomorrow.io/v4"; // 参考 [15]

    // 构造函数注入 RestClient.Builder 和 apiKey
    public ExternalWeatherService(RestClient.Builder builder, @Value("${weather.api.key}") String apiKey) {
        this.restClient = builder.baseUrl(weatherApiBaseUrl).build();
        this.apiKey = apiKey;
    }

    @Cacheable("campusWeather") // 将天气数据缓存起来，缓存名为 "campusWeather"
    public String getCampusWeather(String location) { // location 可以是经纬度或地名
        try {
            // 示例：调用Tomorrow.io API获取实时天气
            // GET /weather/realtime?location=toronto&apikey=XXX [15]
            String response = restClient.get()
                               .uri("/weather/realtime?location={location}&apikey={apiKey}", location, this.apiKey)
                               .retrieve()
                               .body(String.class); // 假设返回的是JSON字符串
            // 实际应用中，这里应该将JSON字符串解析为具体的Java对象
            return response;
        } catch (Exception e) {
            // 处理API调用异常，例如记录日志，返回默认值或错误信息
            System.err.println("Error fetching weather data: " + e.getMessage());
            return "{\"error\": \"Unable to fetch weather data\"}";
        }
    }
}
上述 ExternalWeatherService 演示了如何使用 RestClient 调用天气API，并使用 @Cacheable 注解对结果进行缓存，以提高性能并减少对外部API的依赖。Deepseek API 集成创建一个服务类来调用Deepseek API，例如用于商品描述的智能生成。
认证： 请求头中添加 Authorization: Bearer YOUR_DEEPSEEK_API_KEY 18。
端点： POST https://api.deepseek.com/chat/completions 18。
请求体： JSON格式，包含模型名称（如 deepseek-chat）和消息列表 18。
应用场景： 自动生成商品描述、优化SEO关键词、智能问答等 13。
Java// 文件路径: src/main/java/com/example/yourproject/service/DeepseekService.java
package com.example.yourproject.service;

import com.example.yourproject.dto.DeepseekChatRequest; // 自定义请求DTO
import com.example.yourproject.dto.DeepseekChatResponse; // 自定义响应DTO
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;

@Service
public class DeepseekService {

    private final RestClient restClient;
    private final String apiKey;
    private final String deepseekApiBaseUrl = "https://api.deepseek.com"; // 参考 [19]

    public DeepseekService(RestClient.Builder builder, @Value("${deepseek.api.key}") String apiKey) {
        this.restClient = builder.baseUrl(deepseekApiBaseUrl).build();
        this.apiKey = apiKey;
    }

    public String generateProductDescription(String productTitle) {
        DeepseekChatRequest.Message systemMessage = new DeepseekChatRequest.Message("system", "你是一个帮助用户为二手商品撰写吸引人描述的助手。");
        DeepseekChatRequest.Message userMessage = new DeepseekChatRequest.Message("user", "请为我的商品“" + productTitle + "”生成一段50字左右的描述。");
        
        DeepseekChatRequest requestPayload = new DeepseekChatRequest(
            "deepseek-chat", // 或 "deepseek-reasoner" [18]
            List.of(systemMessage, userMessage),
            false // stream = false
        );

        try {
            DeepseekChatResponse response = restClient.post()
               .uri("/chat/completions")
               .header("Authorization", "Bearer " + this.apiKey)
               .contentType(MediaType.APPLICATION_JSON)
               .body(requestPayload)
               .retrieve()
               .body(DeepseekChatResponse.class);

            if (response!= null && response.getChoices()!= null &&!response.getChoices().isEmpty()) {
                return response.getChoices().get(0).getMessage().getContent();
            }
            return "无法生成描述。";
        } catch (Exception e) {
            System.err.println("Error calling Deepseek API: " + e.getMessage());
            return "调用AI服务时出错。";
        }
    }
}
// DeepseekChatRequest 和 DeepseekChatResponse DTOs 需要自行定义以匹配API的JSON结构
// 例如 DeepseekChatRequest:
// public record DeepseekChatRequest(String model, List<Message> messages, boolean stream) {
//     public record Message(String role, String content) {}
// }
// 例如 DeepseekChatResponse:
// public record DeepseekChatResponse(String id, String object, long created, String model, List<Choice> choices, Usage usage) {
//     public record Choice(int index, Message message, String finish_reason) {}
//     public record Message(String role, String content) {}
//     public record Usage(int prompt_tokens, int completion_tokens, int total_tokens) {}
// }
Redis 缓存
在Spring Boot主配置类或一个专门的配置类上添加 @EnableCaching 注解以启用缓存功能 3。
在需要缓存结果的Service方法上使用 @Cacheable 注解。当方法被调用时，Spring会先检查缓存中是否存在对应键的结果，如果存在则直接返回缓存值，否则执行方法并将结果存入缓存 3。
使用 @CachePut 可以在不影响方法执行的情况下更新缓存。
使用 @CacheEvict 可以从缓存中移除数据，例如当数据更新或删除时。
Java// 示例：在CategoryService中缓存分类列表
// 文件路径: src/main/java/com/example/yourproject/service/CategoryService.java
package com.example.yourproject.service;

import com.example.yourproject.model.Category;
import com.example.yourproject.mapper.CategoryMapper; // 假设有CategoryMapper
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryMapper categoryMapper; // 注入Mapper

    @Autowired
    public CategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Cacheable("categories") // 缓存的名称为 "categories"
    public List<Category> getAllCategories() {
        System.out.println("Fetching categories from database..."); // 用于演示缓存是否生效
        return categoryMapper.findAll(); // 假设CategoryMapper有findAll方法
    }
}

/ 文件路径: src/main/java/com/example/yourproject/service/impl/CategoryServiceImpl.java
package com.example.yourproject.service.impl;

// ... imports ...
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;

@Service
public class CategoryServiceImpl implements CategoryService {

    // ... other code ...

    @Override
    @Cacheable("allCategories") // 缓存的名称为 "allCategories"
    public List<Category> getAllCategories() {
        System.out.println("Fetching all categories from DB..."); // 用于演示缓存是否生效
        return categoryMapper.findAll(); 
    }

    @Override
    @Cacheable(value = "categories", key = "#categoryId")
    public Category getCategoryById(Integer categoryId) {
        System.out.println("Fetching category from DB by ID: " + categoryId);
        return categoryMapper.findById(categoryId);
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "allCategories", allEntries = true) 
    @CachePut(value = "categories", key = "#result.categoryId") // #result 指方法返回值
    public Category createCategory(Category category) throws RuntimeException {
        // ... (创建逻辑)
        categoryMapper.insert(category);
        return category;
    }
    
    // updateCategory 和 deleteCategory 同样可以使用 @CacheEvict 和 @CachePut
}
// 在主应用类或配置类中启用缓存
// import org.springframework.cache.annotation.EnableCaching;
// @SpringBootApplication
// @EnableCaching // 启用缓存
// public class CampusTradeSystemApplication {... }
8.4. AI智能体编排 (简化版阿里云示例)根据用户“简单一点就行”的要求，此处的“智能体编排”将简化为后端服务调用单个部署在云平台（如阿里云模型服务灵积（ModelScope）或函数计算FC部署的AI模型）上的AI应用/模型。目的是体验与云端AI服务的集成。
概念： 真正的智能体编排可能涉及多个AI智能体协同工作，自动规划任务流程 20。对于本学生项目，我们将此简化：Spring Boot后端的一个服务将作为“调用者”，请求一个在阿里云上部署的、具备特定AI功能的模型（例如，通过Model Studio部署的Qwen系列模型 20）。
场景示例：“智能商品标签/分类推荐”

用户在发布商品时输入标题和基本描述。
后端服务（充当“编排器”或“调用者”）将这些文本信息发送给部署在阿里云上的AI代理/模型。该模型可能经过特定提示（Prompt）工程或微调，以执行标签提取或分类任务。
阿里云上的AI模型处理文本，返回推荐的标签列表或商品分类建议。
后端服务接收这些建议，并将其与商品关联起来，或展示给用户供选择。


阿里云模型服务（如Model Studio / DashScope API）：

学生可以在阿里云Model Studio中选择合适的预训练模型（如Qwen系列用于文本理解和生成），通过简单的配置和Prompt设计，将其部署为一个可调用的API服务 20。平台通常会提供API的调用地址、认证方式（如API Key）。
虽然20中的“日程管理助手”示例展示了两个智能体的协作，但本项目可以聚焦于调用单个定制化的AI能力。


Spring Boot Service 实现：

创建一个Service类，使用 RestClient 调用部署在阿里云上的AI模型的API端点。
在请求中传递商品信息（如标题、描述）。
处理从AI模型返回的JSON响应，提取标签或分类信息。


Java// 文件路径: src/main/java/com/example/yourproject/service/AlibabaAiAgentService.java
package com.example.yourproject.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

// 假设的请求和响应DTO
// record AiTaggingRequest(String title, String description) {}
// record AiTaggingResponse(List<String> tags, String suggestedCategory) {}

@Service
public class AlibabaAiAgentService {

    private final RestClient restClient;
    private final String apiKey; // 阿里云服务的API Key
    private final String apiEndpoint; // 阿里云服务的API Endpoint

    public AlibabaAiAgentService(RestClient.Builder builder,
                                 @Value("${alibaba.agent.api.key}") String apiKey,
                                 @Value("${alibaba.agent.api.endpoint}") String apiEndpoint) {
        this.restClient = builder.baseUrl(apiEndpoint).build(); // Endpoint可能包含基础路径
        this.apiKey = apiKey;
        this.apiEndpoint = apiEndpoint; // 仅用于日志或调试
    }

    public /* AiTaggingResponse */ String getSmartTagsAndCategory(String productTitle, String productDescription) {
        // 构造请求体，具体结构取决于您在阿里云上部署的AI服务所期望的格式
        // AiTaggingRequest requestPayload = new AiTaggingRequest(productTitle, productDescription);
        String requestPayloadString = String.format("{\"model\": \"qwen-plus\", \"input\":{\"prompt\":\"请为以下商品信息生成3-5个合适的标签和一个推荐分类：标题-%s，描述-%s\"}, \"parameters\":{}}", productTitle, productDescription);


        try {
            String response = restClient.post()
                //.uri("/predict") // URI路径取决于您部署的服务
               .header("Authorization", "Bearer " + this.apiKey) // 或其他阿里云要求的认证方式
               .contentType(MediaType.APPLICATION_JSON)
               .body(requestPayloadString) // 发送请求体
               .retrieve()
               .body(String.class); // 假设返回JSON字符串，后续可解析为AiTaggingResponse DTO

            // 实际应用中，解析response字符串为AiTaggingResponse对象
            // ObjectMapper objectMapper = new ObjectMapper();
            // return objectMapper.readValue(response, AiTaggingResponse.class);
            return response; // 简化返回
        } catch (Exception e) {
            System.err.println("Error calling Alibaba AI Agent: " + e.getMessage());
            // return new AiTaggingResponse(Collections.emptyList(), "Error");
            return "{\"error\": \"调用阿里云AI服务失败\"}";
        }
    }
}
这种简化的方式满足了集成AI技术和特定云平台（阿里云）的要求，同时避免了引入复杂的多智能体编排框架，更适合课程项目。9. 前端开发 (Vue3)前端负责用户界面和用户体验。
组件结构概览：

建议采用清晰的组件化结构，例如：

src/views/：存放页面级组件 (如 Home.vue, Login.vue, ProductDetail.vue)。
src/components/：存放可复用的UI组件 (如 ProductCard.vue, Navbar.vue, SearchBar.vue)。




状态管理 (可选，但推荐)：

对于中等复杂度的应用，推荐使用Pinia作为Vue3的状态管理库。它能帮助集中管理应用的共享状态，使状态变更可预测且易于调试。


API服务模块：

创建一个或多个JavaScript/TypeScript模块 (例如 src/services/api.js 或 src/services/productService.js) 来封装对后端API的调用。通常使用 axios 或浏览器原生的 fetch API。
这样做可以将API调用逻辑与UI组件分离，提高代码的可维护性和复用性。

JavaScript// 示例: src/services/productService.js (使用axios)
import axios from 'axios';

const API_BASE_URL = process.env.VUE_APP_API_BASE_URL |


| 'http://localhost:8080/api';const apiClient = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
        // Authorization: `Bearer ${localStorage.getItem('user-token')}` // 如果需要token认证
    }
});

// 拦截器，用于在请求发送前附加token
apiClient.interceptors.request.use(config => {
    const token = localStorage.getItem('user-token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, error => {
    return Promise.reject(error);
});


export default {
    getProducts() {
        return apiClient.get('/products');
    },
    getProductById(productId) {
        return apiClient.get(`/products/${productId}`);
    },
    createProduct(productData) {
        return apiClient.post('/products', productData);
    },
    //... 其他API方法
    getWeather() {
        return apiClient.get('/weather/campus');
    },
    generateDescription(title, initialDesc) {
        return apiClient.post(`/ai/generate-description?title=<span class="math-inline">\{encodeURIComponent\(title\)\}&initialDesc\=</span>{encodeURIComponent(initialDesc)}`);
    }
};
```

数据展示：

在Vue组件的模板中，使用 {{ }} (Mustache插值) 绑定数据，使用 v-for 指令渲染列表，使用 v-if/v-else 进行条件渲染。


用户输入与表单：

使用 v-model 指令实现表单输入元素和组件状态之间的双向数据绑定。
进行客户端表单校验，提升用户体验，并在提交到后端前进行初步数据检查。


10. 运行应用后端 (Spring Boot)
确保达梦数据库服务已启动，并且 application.properties 中的数据库连接信息正确无误。
确保Redis服务已启动 (如果配置了Redis)。
导航到项目的 backend 模块目录。
通过Maven运行：
Bashcd backend
mvn spring-boot:run

或者，先打包成JAR文件，然后运行：
Bashmvn package
java -jar target/backend-0.0.1-SNAPSHOT.jar # JAR文件名可能因项目配置而异

6 中有关于通过 mvn spring-boot:run 启动应用的示例。
前端 (Vue3)
导航到项目的 frontend 模块目录。
安装依赖 (如果尚未安装)：
Bashcd frontend
npm install # 或者 yarn install


启动开发服务器：
Bashnpm run serve # 或者 yarn serve

这通常会在 http://localhost:8081 (或类似端口) 启动前端开发服务器，并带有热重载功能。
访问应用
如果前端通过开发服务器运行 (例如 npm run serve)，则通常在 http://localhost:8081 (或其他Vue CLI指定的端口) 访问前端。前端应用会通过配置的API基础URL (如 http://localhost:8080/api) 与后端通信。
集成部署方案 (推荐用于简化部署)：
可以将前端构建后的静态文件（dist 目录中的内容）集成到Spring Boot应用中，由Spring Boot统一提供服务。这通常涉及：

在 vue.config.js (前端项目根目录) 中配置 outputDir，使其指向后端项目的静态资源目录，例如 target/classes/static 或一个中间目录。
JavaScript// vue.config.js
module.exports = {
  outputDir: '../backend/src/main/resources/static/dist_frontend', // 示例路径
  assetsDir: 'static' // 静态资源子目录
};


配置Maven的 maven-resources-plugin (在后端 pom.xml) 或 frontend-maven-plugin (在父 pom.xml 或前端 pom.xml) 来在构建后端时触发前端构建，并将构建产物复制到Spring Boot的静态资源目录下。
参考 6 的做法，通过 maven-resources-plugin 将 frontend/target/dist (假设 vue.config.js 输出到 target/dist) 复制到 backend/target/classes/static/。
如果采用此集成方案，则启动后端应用后，可以直接通过 http://localhost:8080 访问整个应用。这种方式对于学生项目而言，简化了部署流程，形成一个单一的可部署单元。


11. 进一步建议与最佳实践 (可选)
安全性：

引入Spring Security进行用户认证和授权管理。
密码存储务必使用强哈希算法（如BCryptPasswordEncoder）。
考虑API接口的安全性，如防止CSRF、XSS攻击等。


错误处理：

在Spring Boot中设置全局异常处理器 (@ControllerAdvice, @ExceptionHandler)，为API提供统一、规范的错误响应格式。


日志记录：

配置合适的日志框架（如Logback，Spring Boot默认集成）和日志级别，记录关键操作和错误信息，便于调试和问题追踪。


单元测试与集成测试：

为后端Service和Controller编写单元测试 (JUnit, Mockito)。
为前端组件编写单元测试 (Vue Test Utils, Jest)。
考虑编写API集成测试。


API文档：

使用Swagger/OpenAPI (通过Springdoc-openapi等库) 为后端API自动生成交互式文档，方便前端调用和团队协作。


12. 故障排除提示
达梦数据库连接问题：

仔细检查 application.properties 中的 spring.datasource.url, username, password, driver-class-name 是否正确。
确认达梦数据库服务已启动，并且监听的IP和端口与配置一致。
检查防火墙设置是否允许应用服务器访问达梦数据库端口。
确认使用的达梦JDBC驱动版本与数据库版本兼容。


MyBatis Mapper问题：

XML Mapper文件中的 namespace 是否与Mapper接口的完全限定名匹配。
application.properties 中的 mybatis.mapper-locations路径是否正确指向了XML文件。
Mapper方法参数与SQL中的参数占位符 (#{paramName}) 是否匹配，注意 @Param 注解的使用。
检查达梦数据库的SQL方言与MyBatis中编写的SQL是否兼容（例如，日期函数、分页语法等）。


API密钥管理问题：

确保外部API的密钥已正确配置在 application.properties 中或通过环境变量传入。
检查API密钥是否有相应的权限，以及是否超出了使用配额。


CORS (跨域资源共享) 问题：

如果在开发时前端和后端分别运行在不同端口（例如，Vue开发服务器在8081，Spring Boot在8080），浏览器会阻止跨域请求。需要在Spring Boot中配置CORS，例如在Controller类或方法上使用 @CrossOrigin 注解，或配置全局CORS策略。


Vue3构建/运行问题：

检查Node.js和npm/yarn的版本是否符合项目要求。
确保前端代码中配置的后端API端点URL正确无误。
查看浏览器开发者工具的控制台和网络选项卡，获取详细的错误信息。


