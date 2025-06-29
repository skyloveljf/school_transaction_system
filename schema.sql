SET SCHEMA STS_DB; -- Moved to the top

-- SELECT table_name FROM all_tables WHERE owner = 'STS_DB' AND table_name = 'USERS'; -- For checking, can be commented out

-- 删除已存在的表 (使用匿名块安全删除，避免表不存在错误)
-- 注意删除顺序，先删除依赖其他表的表


BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE IF EXISTS MESSAGES';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE IF EXISTS FAVORITES';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE IF EXISTS ORDERS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE IF EXISTS COMMENTS'; -- This will cover both potential "COMMENTS" tables if one was named STS_DB.comments
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE IF EXISTS STS_DB.prohibited_rules'; -- Added drop for prohibited_rules
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE IF EXISTS PRODUCTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE IF EXISTS CONVERSATIONS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE IF EXISTS USERS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE IF EXISTS CATEGORIES';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

-- 用户表 (USERS)
CREATE TABLE USERS (
    user_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    registration_date TIMESTAMP DEFAULT SYSTIMESTAMP,
    last_login_date TIMESTAMP,
    avatar_url VARCHAR(512)

);

COMMENT ON TABLE USERS IS '用户信息表';
COMMENT ON COLUMN USERS.user_id IS '用户ID (主键, 自增)';
COMMENT ON COLUMN USERS.username IS '用户名 (唯一)';
COMMENT ON COLUMN USERS.password_hash IS '加密后的密码';
COMMENT ON COLUMN USERS.email IS '电子邮箱 (唯一)';
COMMENT ON COLUMN USERS.registration_date IS '注册时间';
COMMENT ON COLUMN USERS.last_login_date IS '最后登录时间';
COMMENT ON COLUMN USERS.avatar_url IS '用户头像图片URL';

-- 在USERS表创建后添加role字段
ALTER TABLE STS_DB.USERS ADD COLUMN role VARCHAR(20) DEFAULT 'ROLE_USER';
COMMENT ON COLUMN USERS.role IS '用户角色';

ALTER TABLE STS_DB.USERS ADD bio VARCHAR(500);
COMMENT ON COLUMN USERS.bio IS '用户个人简介';


-- 商品分类表 (CATEGORIES)
CREATE TABLE CATEGORIES (
    category_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description CLOB
);

COMMENT ON TABLE CATEGORIES IS '商品分类表';
COMMENT ON COLUMN CATEGORIES.category_id IS '分类ID (主键, 自增)';
COMMENT ON COLUMN CATEGORIES.category_name IS '分类名称 (唯一)';
COMMENT ON COLUMN CATEGORIES.description IS '分类描述';

-- 商品表 (PRODUCTS)
CREATE TABLE PRODUCTS (
    product_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description CLOB,
    price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'AVAILABLE' NOT NULL,
    image_url VARCHAR(512),
    post_date TIMESTAMP DEFAULT SYSTIMESTAMP,
    views INT DEFAULT 0,
    CONSTRAINT FK_PRODUCT_USER FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE,
    CONSTRAINT FK_PRODUCT_CATEGORY FOREIGN KEY (category_id) REFERENCES CATEGORIES(category_id) ON DELETE CASCADE
);

COMMENT ON TABLE PRODUCTS IS '商品信息表';
COMMENT ON COLUMN PRODUCTS.product_id IS '商品ID (主键, 自增)';
COMMENT ON COLUMN PRODUCTS.user_id IS '发布者用户ID (外键)';
COMMENT ON COLUMN PRODUCTS.category_id IS '商品分类ID (外键)';
COMMENT ON COLUMN PRODUCTS.title IS '商品标题';
COMMENT ON COLUMN PRODUCTS.description IS '商品描述';
COMMENT ON COLUMN PRODUCTS.price IS '商品价格';
COMMENT ON COLUMN PRODUCTS.status IS '商品状态 (AVAILABLE, SOLD等)';
COMMENT ON COLUMN PRODUCTS.image_url IS '商品主图片URL';
COMMENT ON COLUMN PRODUCTS.post_date IS '发布时间';
COMMENT ON COLUMN PRODUCTS.views IS '浏览次数';

-- 插入示例商品分类数据
INSERT INTO CATEGORIES (category_name, description) VALUES
('电子产品', '包括手机、电脑、平板、相机、耳机、充电宝等各类二手电子设备。');

INSERT INTO CATEGORIES (category_name, description) VALUES
('书籍教材', '各类二手教科书、考研资料、小说、杂志等。');

INSERT INTO CATEGORIES (category_name, description) VALUES
('生活用品', '包括宿舍用品、小家电、厨具、装饰品等日常所需。');

INSERT INTO CATEGORIES (category_name, description) VALUES
('服饰鞋包', '二手衣物、鞋子、包包、配饰等，请确保清洁卫生。');

INSERT INTO CATEGORIES (category_name, description) VALUES
('运动户外', '运动器材、户外装备、自行车、球类等。');

-- 订单表 (ORDERS)
CREATE TABLE ORDERS (
    order_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    product_id BIGINT NOT NULL,
    buyer_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    price_at_purchase DECIMAL(10,2) NOT NULL,
    order_time TIMESTAMP DEFAULT SYSTIMESTAMP,
    last_update_time TIMESTAMP DEFAULT SYSTIMESTAMP,
    CONSTRAINT FK_ORDER_PRODUCT FOREIGN KEY (product_id) REFERENCES PRODUCTS(product_id) ON DELETE CASCADE,
    CONSTRAINT FK_ORDER_BUYER FOREIGN KEY (buyer_id) REFERENCES USERS(user_id) ON DELETE CASCADE,
    CONSTRAINT FK_ORDER_SELLER FOREIGN KEY (seller_id) REFERENCES USERS(user_id) ON DELETE CASCADE
);

COMMENT ON TABLE ORDERS IS '订单信息表';
COMMENT ON COLUMN ORDERS.order_id IS '订单ID (主键, 自增)';
COMMENT ON COLUMN ORDERS.product_id IS '商品ID (外键)';
COMMENT ON COLUMN ORDERS.buyer_id IS '买家用户ID (外键)';
COMMENT ON COLUMN ORDERS.seller_id IS '卖家用户ID (外键)';
COMMENT ON COLUMN ORDERS.status IS '订单状态 (PENDING, COMPLETED等)';
COMMENT ON COLUMN ORDERS.price_at_purchase IS '下单时商品价格';
COMMENT ON COLUMN ORDERS.order_time IS '订单创建时间';
COMMENT ON COLUMN ORDERS.last_update_time IS '订单最后更新时间';

-- 收藏表 (FAVORITES)
CREATE TABLE FAVORITES (
    favorite_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    favorite_time TIMESTAMP DEFAULT SYSTIMESTAMP,
    CONSTRAINT FK_FAVORITE_USER FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE,
    CONSTRAINT FK_FAVORITE_PRODUCT FOREIGN KEY (product_id) REFERENCES PRODUCTS(product_id) ON DELETE CASCADE,
    CONSTRAINT UK_USER_PRODUCT_FAVORITE UNIQUE (user_id, product_id)
);

COMMENT ON TABLE FAVORITES IS '用户收藏表';
COMMENT ON COLUMN FAVORITES.favorite_id IS '收藏记录ID (主键, 自增)';
COMMENT ON COLUMN FAVORITES.user_id IS '用户ID (外键)';
COMMENT ON COLUMN FAVORITES.product_id IS '商品ID (外键)';
COMMENT ON COLUMN FAVORITES.favorite_time IS '收藏时间';

-- 评论表 (COMMENTS) - 主评论表
CREATE TABLE COMMENTS (
    comment_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    product_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    parent_comment_id BIGINT, -- 用于指向父评论的ID
    content CLOB NOT NULL,
    created_time TIMESTAMP DEFAULT SYSTIMESTAMP,
    CONSTRAINT FK_COMMENT_PRODUCT FOREIGN KEY (product_id) REFERENCES PRODUCTS(product_id) ON DELETE CASCADE,
    CONSTRAINT FK_COMMENT_USER FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE,
    CONSTRAINT FK_COMMENT_PARENT FOREIGN KEY (parent_comment_id) REFERENCES COMMENTS(comment_id) ON DELETE CASCADE -- 外键约束也使用 parent_comment_id
);

COMMENT ON TABLE COMMENTS IS '商品评论表';
COMMENT ON COLUMN COMMENTS.comment_id IS '评论ID (主键, 自增)';
COMMENT ON COLUMN COMMENTS.product_id IS '关联的商品ID (外键)';
COMMENT ON COLUMN COMMENTS.user_id IS '发表评论的用户ID (外键)';
COMMENT ON COLUMN COMMENTS.parent_comment_id IS '父评论ID (用于回复功能)'; -- 列注释也指明 parent_comment_id
COMMENT ON COLUMN COMMENTS.content IS '评论内容';
COMMENT ON COLUMN COMMENTS.created_time IS '评论发表时间';

-- 会话表 (CONVERSATIONS)
CREATE TABLE CONVERSATIONS (
    conversation_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    last_message_time TIMESTAMP DEFAULT SYSTIMESTAMP,
    CONSTRAINT FK_CONV_USER1 FOREIGN KEY (user1_id) REFERENCES USERS(user_id) ON DELETE CASCADE,
    CONSTRAINT FK_CONV_USER2 FOREIGN KEY (user2_id) REFERENCES USERS(user_id) ON DELETE CASCADE,
    CONSTRAINT UK_CONVERSATION UNIQUE (user1_id, user2_id)
);

COMMENT ON TABLE CONVERSATIONS IS '用户私信会话表';
COMMENT ON COLUMN CONVERSATIONS.conversation_id IS '会话ID (主键, 自增)';
COMMENT ON COLUMN CONVERSATIONS.user1_id IS '用户1ID';
COMMENT ON COLUMN CONVERSATIONS.user2_id IS '用户2ID';
COMMENT ON COLUMN CONVERSATIONS.last_message_time IS '最后消息时间';

-- 私信表 (MESSAGES)
CREATE TABLE MESSAGES (
    message_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    content VARCHAR(1000) NOT NULL,
    send_time TIMESTAMP DEFAULT SYSTIMESTAMP,
    is_read NUMBER(1) DEFAULT 0,
    CONSTRAINT FK_MESSAGE_CONV FOREIGN KEY (conversation_id) REFERENCES CONVERSATIONS(conversation_id) ON DELETE CASCADE,
    CONSTRAINT FK_MESSAGE_SENDER FOREIGN KEY (sender_id) REFERENCES USERS(user_id) ON DELETE CASCADE,
    CONSTRAINT FK_MESSAGE_RECEIVER FOREIGN KEY (receiver_id) REFERENCES USERS(user_id) ON DELETE CASCADE
);

COMMENT ON TABLE MESSAGES IS '私信消息表';
COMMENT ON COLUMN MESSAGES.message_id IS '消息ID (主键, 自增)';
COMMENT ON COLUMN MESSAGES.conversation_id IS '会话ID';
COMMENT ON COLUMN MESSAGES.sender_id IS '发送者ID';
COMMENT ON COLUMN MESSAGES.receiver_id IS '接收者ID';
COMMENT ON COLUMN MESSAGES.content IS '消息内容';
COMMENT ON COLUMN MESSAGES.send_time IS '发送时间';
COMMENT ON COLUMN MESSAGES.is_read IS '是否已读(0:未读, 1:已读)';

-- 违禁规则表 (PROHIBITED_RULES)
CREATE TABLE STS_DB.prohibited_rules (
    rule_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    rule_name VARCHAR(100) NOT NULL,
    rule_description VARCHAR(500) NOT NULL,
    is_active NUMBER(1) DEFAULT 1 NOT NULL, -- DM 使用 NUMBER(1) 表示布尔值, 1=激活, 0=禁用
    created_time TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    updated_time TIMESTAMP NULL,
    created_by BIGINT NULL,
    CONSTRAINT fk_prohibited_rules_created_by FOREIGN KEY (created_by) REFERENCES STS_DB.users(user_id) ON DELETE SET NULL -- 如果创建者用户被删除，则将created_by设为NULL
);

COMMENT ON TABLE STS_DB.prohibited_rules IS '违禁规则表 - 用于商品自动审核';
COMMENT ON COLUMN STS_DB.prohibited_rules.rule_id IS '规则ID，主键';
COMMENT ON COLUMN STS_DB.prohibited_rules.rule_name IS '规则名称';
COMMENT ON COLUMN STS_DB.prohibited_rules.rule_description IS '规则描述';
COMMENT ON COLUMN STS_DB.prohibited_rules.is_active IS '是否激活，1=激活，0=禁用';
COMMENT ON COLUMN STS_DB.prohibited_rules.created_time IS '创建时间';
COMMENT ON COLUMN STS_DB.prohibited_rules.updated_time IS '更新时间';
COMMENT ON COLUMN STS_DB.prohibited_rules.created_by IS '创建者用户ID';


-- 索引
CREATE INDEX IDX_PRODUCT_TITLE ON PRODUCTS(title);
CREATE INDEX IDX_PRODUCT_USER_ID ON PRODUCTS(user_id);
CREATE INDEX IDX_PRODUCT_CATEGORY_ID ON PRODUCTS(category_id);
CREATE INDEX IDX_ORDER_BUYER_ID ON ORDERS(buyer_id);
CREATE INDEX IDX_ORDER_SELLER_ID ON ORDERS(seller_id);
CREATE INDEX IDX_FAVORITE_USER_ID ON FAVORITES(user_id);
CREATE INDEX IDX_COMMENT_PRODUCT_ID ON COMMENTS(product_id);
CREATE INDEX IDX_COMMENT_USER_ID ON COMMENTS(user_id);
CREATE INDEX IDX_COMMENT_PARENT_ID ON COMMENTS(parent_comment_id);
CREATE INDEX IDX_CONVERSATION_USER1 ON CONVERSATIONS(user1_id);
CREATE INDEX IDX_CONVERSATION_USER2 ON CONVERSATIONS(user2_id);
CREATE INDEX IDX_MESSAGE_CONVERSATION ON MESSAGES(conversation_id);
CREATE INDEX IDX_MESSAGE_SENDER ON MESSAGES(sender_id);
CREATE INDEX IDX_MESSAGE_RECEIVER ON MESSAGES(receiver_id);
CREATE INDEX IDX_MESSAGE_IS_READ ON MESSAGES(is_read);
CREATE INDEX idx_prohibited_rules_active ON STS_DB.prohibited_rules(is_active);
CREATE INDEX idx_prohibited_rules_created_by ON STS_DB.prohibited_rules(created_by);
CREATE INDEX idx_prohibited_rules_created_time ON STS_DB.prohibited_rules(created_time);



-- 插入一些示例违禁规则
-- 注意: created_by 字段引用 USERS(user_id)。
-- 将 created_by 设置为 NULL，因为初始规则可能不由特定用户创建，或者对应用户尚不存在。
INSERT INTO STS_DB.prohibited_rules (rule_name, rule_description, is_active, created_by) VALUES
('禁止色情内容', '不允许发布包含色情、裸体或性暗示内容的商品图片', 1, NULL),
('禁止暴力内容', '不允许发布包含暴力、血腥或恐怖内容的商品图片', 1, NULL),
('禁止违法物品', '不允许发布枪支、毒品、假币等违法物品的图片', 1, NULL),
('禁止侵权内容', '不允许发布侵犯他人知识产权的商品图片', 1, NULL),
('禁止欺诈信息', '不允许发布虚假、误导性或欺诈性的商品信息和图片', 1, NULL);