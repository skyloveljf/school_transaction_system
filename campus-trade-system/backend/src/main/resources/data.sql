-- 确保 schema.sql 已执行且表已创建
-- 注意：DM数据库中，如果数据已存在并违反唯一约束，INSERT会失败。
-- Script for DM Database

-- 插入用户 (密码是明文'password'，实际应用中应由注册逻辑处理加密)
-- 在UserServiceImpl中注册时会进行加密，这里为了data.sql简单，直接插入预加密的密码
-- 例如，'password'的BCrypt哈希是 '$2a$10$abcdefghijklmnopqrstuv' (这只是一个示例哈希，你需要用你的Password Generator生成)
-- 为了简单，我们先插入明文，然后假设通过一个初始化服务或手动更新密码。
-- 或者，更好的方式是在Java代码中进行初始化，以便使用PasswordEncoder。
-- 这里我们假设密码是预先哈希过的。
-- 使用 BCrypt Online Generator, 'password' -> $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- 'adminpass' -> $2a$10$E.x0t7n8L02L.1rZ0hB6bu/yF1u0YwzM6kQv3u1.qD8jO9X0qJd2q
-- 'userpass' -> $2a$10$s4GkSumpXqg8Y1j3kZvMNeIZBiWpIPbfL0ImE60ivYsLrvG/za8Zu
SET SCHEMA tongrenG;
DELETE FROM USERS WHERE USER_ID = 5;

-- INSERT INTO tongrenG.USERS (username, password_hash, email, registration_date)
-- VALUES ('testuser', '$2a$10$l5l3nJRubCfZcFK/ROHkNegHEMAjNCdYYRsqQZUNZvKIiXVNjOSl2', 'test@example.com', SYSTIMESTAMP);
-- SELECT user_id, username, email, registration_date FROM tongrenG.USERS;
MERGE INTO USERS u
USING (SELECT 1 AS dual) ON (u.username = 'admin')
WHEN NOT MATCHED THEN
    INSERT (username, password_hash, email, registration_date, avatar_url)
    VALUES ('admin', '$2a$10$E.x0t7n8L02L.1rZ0hB6bu/yF1u0YwzM6kQv3u1.qD8jO9X0qJd2q', 'admin@example.com', SYSTIMESTAMP, '/media/avatars/default_avatar.png');

MERGE INTO USERS u
USING (SELECT 1 AS dual) ON (u.username = 'user1')
WHEN NOT MATCHED THEN
    INSERT (username, password_hash, email, registration_date, avatar_url)
    VALUES ('user1', '$2a$10$s4GkSumpXqg8Y1j3kZvMNeIZBiWpIPbfL0ImE60ivYsLrvG/za8Zu', 'user1@example.com', SYSTIMESTAMP, '/media/avatars/avatar1.png');

MERGE INTO USERS u
USING (SELECT 1 AS dual) ON (u.username = 'user2')
WHEN NOT MATCHED THEN
    INSERT (username, password_hash, email, registration_date, avatar_url)
    VALUES ('user2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'user2@example.com', SYSTIMESTAMP, '/media/avatars/avatar2.png');

-- 插入商品分类
MERGE INTO CATEGORIES c
USING (SELECT 1 AS dual) ON (c.category_name = '电子产品')
WHEN NOT MATCHED THEN
    INSERT (category_name, description) VALUES ('电子产品', '各类手机、电脑、数码配件等');

MERGE INTO CATEGORIES c
USING (SELECT 1 AS dual) ON (c.category_name = '书籍教材')
WHEN NOT MATCHED THEN
    INSERT (category_name, description) VALUES ('书籍教材', '二手教材、考研资料、课外读物等');

MERGE INTO CATEGORIES c
USING (SELECT 1 AS dual) ON (c.category_name = '生活用品')
WHEN NOT MATCHED THEN
    INSERT (category_name, description) VALUES ('生活用品', '宿舍神器、日常用品、小家电等');

MERGE INTO CATEGORIES c
USING (SELECT 1 AS dual) ON (c.category_name = '服饰鞋包')
WHEN NOT MATCHED THEN
    INSERT (category_name, description) VALUES ('服饰鞋包', '潮流服饰、运动鞋、时尚包包等');


MERGE INTO PRODUCTS p
USING (SELECT 1 AS dual) ON (p.title = '九成新二手笔记本电脑' AND p.user_id = (SELECT user_id FROM USERS WHERE username = 'user1'))
WHEN NOT MATCHED THEN
    INSERT (user_id, category_id, title, description, price, status, image_url, post_date, views)
    VALUES (
        (SELECT user_id FROM USERS WHERE username = 'user1'), 
        (SELECT category_id FROM CATEGORIES WHERE category_name = '电子产品'), 
        '九成新二手笔记本电脑', 
        '自用九成新笔记本，配置尚可，适合学生党日常使用和编程学习。轻微使用痕迹，电池健康。', 
        1200.00, 
        'AVAILABLE', 
        '/media/product-images/laptop1.jpg', 
        SYSTIMESTAMP, 
        25
    );

MERGE INTO PRODUCTS p
USING (SELECT 1 AS dual) ON (p.title = '大学高数教材全套' AND p.user_id = (SELECT user_id FROM USERS WHERE username = 'user2'))
WHEN NOT MATCHED THEN
    INSERT (user_id, category_id, title, description, price, status, image_url, post_date, views)
    VALUES (
        (SELECT user_id FROM USERS WHERE username = 'user2'), 
        (SELECT category_id FROM CATEGORIES WHERE category_name = '书籍教材'), 
        '大学高数教材全套', 
        '大学高等数学上下册，同步辅导习题集，9成新，笔记少，适合考研或补习。', 
        80.50, 
        'AVAILABLE', 
        '/media/product-images/math_books.jpg', 
        SYSTIMESTAMP, 
        10
    );

MERGE INTO PRODUCTS p
USING (SELECT 1 AS dual) ON (p.title = '多功能宿舍小煮锅' AND p.user_id = (SELECT user_id FROM USERS WHERE username = 'admin'))
WHEN NOT MATCHED THEN
    INSERT (user_id, category_id, title, description, price, status, image_url, post_date, views)
    VALUES (
        (SELECT user_id FROM USERS WHERE username = 'admin'), 
        (SELECT category_id FROM CATEGORIES WHERE category_name = '生活用品'), 
        '多功能宿舍小煮锅', 
        '1.5L容量，可煮面、火锅、蒸煮，不粘内胆，功率小宿舍安全可用。购入半年，使用次数不多。', 
        45.00, 
        'SOLD', 
        '/media/product-images/pot1.jpg', 
        SYSTIMESTAMP - INTERVAL '1' DAY, 
        55 
    );
    
MERGE INTO ORDERS o
USING (
    SELECT 
        (SELECT product_id FROM PRODUCTS WHERE title = '多功能宿舍小煮锅' AND user_id = (SELECT user_id FROM USERS WHERE username = 'admin')) as p_id,
        (SELECT user_id FROM USERS WHERE username = 'user1') as b_id
) src ON (o.product_id = src.p_id AND o.buyer_id = src.b_id AND o.status = 'COMPLETED')
WHEN NOT MATCHED THEN
    INSERT (product_id, buyer_id, seller_id, price_at_purchase, status, order_time, last_update_time)
    VALUES (
        src.p_id,  -- 直接使用 src 中的值，避免重复查询
        src.b_id,
        (SELECT user_id FROM USERS WHERE username = 'admin'),
        45.00,
        'COMPLETED',
        SYSTIMESTAMP - 12/24,  -- 12 小时前（达梦用数值计算代替 INTERVAL）
        SYSTIMESTAMP - 11/24   -- 11 小时前
    )
WHERE src.p_id IS NOT NULL AND src.b_id IS NOT NULL;  -- 达梦可能不支持 WHERE 子句，如果报错则删除

MERGE INTO FAVORITES fav
USING (
    SELECT 
        (SELECT user_id FROM USERS WHERE username = 'user2') as u_id,
        (SELECT product_id FROM PRODUCTS WHERE title = '九成新二手笔记本电脑' AND user_id = (SELECT user_id FROM USERS WHERE username = 'user1')) as p_id
) src ON (fav.user_id = src.u_id AND fav.product_id = src.p_id)
WHEN NOT MATCHED THEN
    INSERT (user_id, product_id, favorite_time)
    VALUES (
        src.u_id,  -- 直接使用 USING 子句中的值，避免重复查询
        src.p_id,
        SYSTIMESTAMP
    )
WHERE src.u_id IS NOT NULL AND src.p_id IS NOT NULL;  -- 达梦可能不支持 WHERE，若报错则删除

