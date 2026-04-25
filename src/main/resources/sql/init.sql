-- 创建数据库
CREATE DATABASE IF NOT EXISTS tools_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE tools_db;


-- 员工表（管理员）
CREATE TABLE employee
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    username    VARCHAR(32) NOT NULL UNIQUE COMMENT '用户名',
    name        VARCHAR(32) COMMENT '姓名',
    password    VARCHAR(64) NOT NULL COMMENT '密码',
    phone       VARCHAR(11) COMMENT '手机号',
    sex         VARCHAR(2) COMMENT '性别',
    id_number   VARCHAR(18) COMMENT '身份证号',
    status      INT DEFAULT 1 COMMENT '状态 0:禁用，1:正常',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_user BIGINT COMMENT '创建人',
    update_user BIGINT COMMENT '更新人'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='员工信息表';

-- 用户表
CREATE TABLE user
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    openid      VARCHAR(45) COMMENT '微信用户唯一标识',
    name        VARCHAR(32) COMMENT '姓名',
    phone       VARCHAR(11) COMMENT '手机号',
    sex         VARCHAR(2) COMMENT '性别',
    id_number   VARCHAR(18) COMMENT '身份证号',
    avatar      VARCHAR(500) COMMENT '头像',
    status      INT DEFAULT 1 COMMENT '状态 0:禁用，1:正常',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='用户信息表';

-- 分类表
CREATE TABLE category
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    type        INT COMMENT '类型 1:商品分类 2:套餐分类',
    name        VARCHAR(32) NOT NULL COMMENT '分类名称',
    sort        INT DEFAULT 0 COMMENT '顺序',
    status      INT DEFAULT 1 COMMENT '状态 0:禁用，1:正常',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_user BIGINT COMMENT '创建人',
    update_user BIGINT COMMENT '更新人'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='分类表';

-- 商品表
CREATE TABLE product
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(32) NOT NULL COMMENT '商品名称',
    category_id BIGINT COMMENT '分类id',
    price       DECIMAL(10, 2) COMMENT '商品价格',
    image       VARCHAR(255) COMMENT '图片路径',
    description VARCHAR(255) COMMENT '描述',
    status      INT DEFAULT 1 COMMENT '状态 0:停售 1:起售',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_user BIGINT COMMENT '创建人',
    update_user BIGINT COMMENT '更新人'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='商品表';

-- 订单表
CREATE TABLE orders
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    order_number  VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    user_id       BIGINT COMMENT '用户id',
    total_amount  DECIMAL(10, 2) COMMENT '实收金额',
    status        INT DEFAULT 1 COMMENT '订单状态 1:待付款 2:待发货 3:已发货 4:已完成 5:已取消',
    pay_method    INT COMMENT '支付方式 1:微信 2:支付宝',
    pay_status    INT DEFAULT 0 COMMENT '支付状态 0:未支付 1:已支付 2:退款',
    order_time    DATETIME COMMENT '下单时间',
    pay_time      DATETIME COMMENT '付款时间',
    delivery_time DATETIME COMMENT '发货时间',
    complete_time DATETIME COMMENT '完成时间',
    cancel_time   DATETIME COMMENT '取消时间',
    remark        VARCHAR(255) COMMENT '备注',
    create_time   DATETIME COMMENT '创建时间',
    update_time   DATETIME COMMENT '更新时间'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='订单表';

-- 插入测试数据

-- 管理员账号（密码：123456）
INSERT INTO employee (username, name, password, phone, sex, id_number, status, create_time, update_time)
VALUES ('admin', '系统管理员', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138000', '1',
        '110101199001011234', 1, NOW(), NOW()),
       ('zhangsan', '张三', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138001', '1',
        '110101199002022345', 1, NOW(), NOW()),
       ('lisi', '李四', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138002', '0',
        '110101199003033456', 1, NOW(), NOW());

-- 普通用户
INSERT INTO user (openid, name, phone, sex, avatar, status, create_time, update_time)
VALUES ('oWxXXXXXXXXXXXXXXXXXXXXXXXXXXX1', '王五', '13900139000', '1',
        'https://api.dicebear.com/7.x/avataaars/svg?seed=user1', 1, NOW(), NOW()),
       ('oWxXXXXXXXXXXXXXXXXXXXXXXXXXXX2', '赵六', '13900139001', '0',
        'https://api.dicebear.com/7.x/avataaars/svg?seed=user2', 1, NOW(), NOW()),
       ('oWxXXXXXXXXXXXXXXXXXXXXXXXXXXX3', '孙七', '13900139002', '1',
        'https://api.dicebear.com/7.x/avataaars/svg?seed=user3', 1, NOW(), NOW()),
       ('oWxXXXXXXXXXXXXXXXXXXXXXXXXXXX4', '周八', '13900139003', '0',
        'https://api.dicebear.com/7.x/avataaars/svg?seed=user4', 1, NOW(), NOW()),
       ('oWxXXXXXXXXXXXXXXXXXXXXXXXXXXX5', '吴九', '13900139004', '1',
        'https://api.dicebear.com/7.x/avataaars/svg?seed=user5', 1, NOW(), NOW());

-- 商品分类
INSERT INTO category (type, name, sort, status, create_time, update_time)
VALUES (1, '电子产品', 1, 1, NOW(), NOW()),
       (1, '服装服饰', 2, 1, NOW(), NOW()),
       (1, '食品饮料', 3, 1, NOW(), NOW()),
       (1, '图书文具', 4, 1, NOW(), NOW()),
       (1, '生活用品', 5, 1, NOW(), NOW()),
       (2, '优惠套餐', 1, 1, NOW(), NOW()),
       (2, '组合套装', 2, 1, NOW(), NOW());

-- 商品信息
INSERT IGNORE INTO product (name, category_id, price, image, description, status, create_time, update_time)
VALUES ('iPhone 15 Pro', 1, 8999.00, 'https://picsum.photos/seed/iphone15/400/300', '苹果最新款手机，A17 Pro芯片', 1,
        NOW(), NOW()),
       ('MacBook Air M3', 1, 9499.00, 'https://picsum.photos/seed/macbook/400/300', '轻薄便携，M3芯片强大性能', 1, NOW(),
        NOW()),
       ('AirPods Pro 2', 1, 1899.00, 'https://picsum.photos/seed/airpods/400/300', '主动降噪，空间音频', 1, NOW(),
        NOW()),
       ('iPad Air', 1, 4799.00, 'https://picsum.photos/seed/ipad/400/300', '10.9英寸 Liquid 视网膜显示屏', 1, NOW(),
        NOW()),
       ('小米14', 1, 3999.00, 'https://picsum.photos/seed/xiaomi14/400/300', '徕卡光学镜头，骁龙8 Gen3', 1, NOW(),
        NOW()),
       ('华为MatePad', 1, 2499.00, 'https://picsum.photos/seed/matepad/400/300', '11英寸全面屏，鸿蒙系统', 1, NOW(),
        NOW()),
       ('男士休闲衬衫', 2, 199.00, 'https://picsum.photos/seed/shirt/400/300', '纯棉材质，舒适透气', 1, NOW(), NOW()),
       ('运动跑鞋', 2, 599.00, 'https://picsum.photos/seed/shoes/400/300', '轻便缓震，适合跑步健身', 1, NOW(), NOW()),
       ('双肩背包', 2, 299.00, 'https://picsum.photos/seed/backpack/400/300', '大容量，防水耐磨', 1, NOW(), NOW()),
       ('牛仔裤', 2, 259.00, 'https://picsum.photos/seed/jeans/400/300', '经典款式，修身版型', 1, NOW(), NOW()),
       ('有机绿茶', 3, 89.00, 'https://picsum.photos/seed/tea/400/300', '高山茶叶，清香回甘', 1, NOW(), NOW()),
       ('进口咖啡豆', 3, 128.00, 'https://picsum.photos/seed/coffee/400/300', '阿拉比卡咖啡豆，中度烘焙', 1, NOW(),
        NOW()),
       ('坚果礼盒', 3, 168.00, 'https://picsum.photos/seed/nuts/400/300', '多种坚果，营养健康', 1, NOW(), NOW()),
       ('巧克力套装', 3, 99.00, 'https://picsum.photos/seed/chocolate/400/300', '比利时进口，口感丝滑', 1, NOW(), NOW()),
       ('Java编程思想', 4, 108.00, 'https://picsum.photos/seed/javabook/400/300', '经典Java教程，深入浅出', 1, NOW(),
        NOW()),
       ('Python从入门到精通', 4, 79.00, 'https://picsum.photos/seed/pythonbook/400/300', '零基础学习Python编程', 1,
        NOW(), NOW()),
       ('文具套装', 4, 49.00, 'https://picsum.photos/seed/stationery/400/300', '包含笔、本子、便签等', 1, NOW(), NOW()),
       ('保温杯', 5, 89.00, 'https://picsum.photos/seed/cup/400/300', '304不锈钢，保温24小时', 1, NOW(), NOW()),
       ('毛巾套装', 5, 59.00, 'https://picsum.photos/seed/towel/400/300', '纯棉柔软，吸水性强', 1, NOW(), NOW()),
       ('收纳盒', 5, 39.00, 'https://picsum.photos/seed/box/400/300', '多功能收纳，节省空间', 1, NOW(), NOW());

-- 订单数据
INSERT IGNORE INTO orders (order_number, user_id, total_amount, status, pay_method, pay_status, order_time, pay_time,
                           remark, create_time, update_time)
VALUES ('ORD202401010001', 1, 8999.00, 4, 1, 1, '2024-01-01 10:30:00', '2024-01-01 10:35:00', '购买iPhone 15 Pro',
        NOW(), NOW()),
       ('ORD202401020002', 2, 1899.00, 4, 1, 1, '2024-01-02 14:20:00', '2024-01-02 14:25:00', '购买AirPods Pro 2',
        NOW(), NOW()),
       ('ORD202401030003', 3, 599.00, 3, 1, 1, '2024-01-03 09:15:00', '2024-01-03 09:20:00', '购买运动跑鞋', NOW(),
        NOW()),
       ('ORD202401040004', 4, 1089.00, 2, 2, 1, '2024-01-04 16:45:00', '2024-01-04 16:50:00', '多件商品组合', NOW(),
        NOW()),
       ('ORD202401050005', 5, 299.00, 1, 1, 0, '2024-01-05 11:00:00', NULL, '待付款订单', NOW(), NOW()),
       ('ORD202401060006', 1, 4799.00, 4, 1, 1, '2024-01-06 13:30:00', '2024-01-06 13:35:00', '购买iPad Air', NOW(),
        NOW()),
       ('ORD202401070007', 2, 168.00, 4, 1, 1, '2024-01-07 15:20:00', '2024-01-07 15:25:00', '购买坚果礼盒', NOW(),
        NOW()),
       ('ORD202401080008', 3, 9499.00, 5, 1, 2, '2024-01-08 10:00:00', '2024-01-08 10:05:00', '已取消订单', NOW(),
        NOW()),
       ('ORD202401090009', 4, 3999.00, 4, 2, 1, '2024-01-09 17:30:00', '2024-01-09 17:35:00', '购买小米14', NOW(),
        NOW()),
       ('ORD202401100010', 5, 259.00, 4, 1, 1, '2024-01-10 12:00:00', '2024-01-10 12:05:00', '购买牛仔裤', NOW(),
        NOW());

-- 创建索引
CREATE INDEX idx_employee_username ON employee (username);
CREATE INDEX idx_user_openid ON user (openid);
CREATE INDEX idx_product_category ON product (category_id);
CREATE INDEX idx_orders_user ON orders (user_id);
CREATE INDEX idx_orders_number ON orders (order_number);