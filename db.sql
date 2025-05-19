CREATE TABLE users (
  id INT PRIMARY KEY IDENTITY(1,1),
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  nickname VARCHAR(50) NOT NULL,
  avatar_url VARCHAR(255),
  email VARCHAR(100) NOT NULL UNIQUE,
  bio TEXT,
  role VARCHAR(10) DEFAULT 'user',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON COLUMN users.username IS '用户名';
COMMENT ON COLUMN users.password IS '密码（加密存储）';
COMMENT ON COLUMN users.nickname IS '昵称';
COMMENT ON COLUMN users.avatar_url IS '头像URL';
COMMENT ON COLUMN users.email IS '邮箱';
COMMENT ON COLUMN users.bio IS '个人简介';
COMMENT ON COLUMN users.role IS '用户角色';

-- 商品表
CREATE TABLE product (
  id INT PRIMARY KEY IDENTITY(1,1),
  title VARCHAR(100) NOT NULL,
  description TEXT NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  image_url VARCHAR(255) NOT NULL,
  seller_id INT NOT NULL,
  status VARCHAR(20) DEFAULT 'available',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON COLUMN product.title IS '商品标题';
COMMENT ON COLUMN product.description IS '商品描述';
COMMENT ON COLUMN product.price IS '商品价格';
COMMENT ON COLUMN product.image_url IS '封面图URL';
COMMENT ON COLUMN product.seller_id IS '卖家ID';
COMMENT ON COLUMN product.status IS '商品状态';

CREATE INDEX idx_seller_status ON product(seller_id, status);

-- 收藏表
CREATE TABLE favorite (
  id INT PRIMARY KEY IDENTITY(1,1),
  user_id INT NOT NULL,
  product_id INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT unique_favorite UNIQUE(user_id, product_id)
);

CREATE INDEX idx_user_fav ON favorite(user_id, created_at);

-- 评论表（表名由 comment 改为 comments）
CREATE TABLE comments (
  id INT PRIMARY KEY IDENTITY(1,1),
  product_id INT NOT NULL,
  user_id INT NOT NULL,
  content TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_product_comments ON comments(product_id, created_at);

-- 私信表
CREATE TABLE message (
  id INT PRIMARY KEY IDENTITY(1,1),
  sender_id INT NOT NULL,
  receiver_id INT NOT NULL,
  content TEXT NOT NULL,
  is_read SMALLINT DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_conversation ON message(sender_id, receiver_id, created_at);
CREATE INDEX idx_unread ON message(receiver_id, is_read);

-- 商品图片表
CREATE TABLE product_image (
  id INT PRIMARY KEY IDENTITY(1,1),
  product_id INT NOT NULL,
  image_url VARCHAR(255) NOT NULL,
  sort_order INT DEFAULT 0
);

CREATE INDEX idx_product_images ON product_image(product_id, sort_order);

-- 外键约束（达梦建议单独添加）
ALTER TABLE product ADD CONSTRAINT fk_product_seller FOREIGN KEY (seller_id) REFERENCES users(id);
ALTER TABLE favorite ADD CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE favorite ADD CONSTRAINT fk_favorite_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE;
ALTER TABLE comments ADD CONSTRAINT fk_comment_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE;
ALTER TABLE comments ADD CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE message ADD CONSTRAINT fk_message_sender FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE message ADD CONSTRAINT fk_message_receiver FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE product_image ADD CONSTRAINT fk_product_image_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE;