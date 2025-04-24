-- 数据库的名称是 student_platform

-- 记录创建的各项数据表

-- ! 用户表

CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    credit_score INT DEFAULT 100,
    status ENUM('ACTIVE', 'FROZEN', 'BANNED') DEFAULT 'ACTIVE',
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);


-- ! 任务表

CREATE TABLE tasks (
    task_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    deadline DATETIME,
    reward DECIMAL(10, 2) NOT NULL,
    status ENUM('PENDING', 'IN_PROGRESS', 'SUBMITTED', 'COMPLETED', 'CANCELLED', 'EXPIRED') DEFAULT 'PENDING'
);

-- ? 枚举值	含义说明
--  PENDING	待接单
--  IN_PROGRESS	接单人已确认，进行中
--  SUBMITTED	接单者提交成果，待确认
--  COMPLETED	发布者确认完成
--  CANCELLED	发布者或系统取消
--  EXPIRED	截止时间已过，无人接单


-- ! 接单表

CREATE TABLE orders (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT NOT NULL,
    poster_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    order_status ENUM('APPLIED', 'ACCEPTED', 'SUBMITTED', 'CONFIRMED', 'CANCELLED') DEFAULT 'APPLIED',
    payment_status ENUM('UNPAID', 'PAID') DEFAULT 'UNPAID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    confirmed_at DATETIME DEFAULT NULL,

    CONSTRAINT fk_task FOREIGN KEY (task_id) REFERENCES tasks(task_id),
    CONSTRAINT fk_poster FOREIGN KEY (poster_id) REFERENCES users(user_id),
    CONSTRAINT fk_receiver FOREIGN KEY (receiver_id) REFERENCES users(user_id)
);


-- ? 接单状态 order_status：

-- 枚举值	含义说明
-- APPLIED	接单人提交申请，等待发布者选择
-- ACCEPTED	发布者已选定接单人
-- SUBMITTED	接单人已提交成果
-- CONFIRMED	发布者确认任务完成
-- CANCELLED	任意一方取消或任务终止

-- ? 支付状态 payment_status：

-- 枚举值	含义说明
-- UNPAID	尚未支付
-- PAID	发布者已支付报酬


-- ! 评价表

CREATE TABLE reviews (
    review_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT NOT NULL,
    reviewer_id BIGINT NOT NULL,
    reviewee_id BIGINT NOT NULL,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    content TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_review_task FOREIGN KEY (task_id) REFERENCES tasks(task_id),
    CONSTRAINT fk_reviewer FOREIGN KEY (reviewer_id) REFERENCES users(user_id),
    CONSTRAINT fk_reviewee FOREIGN KEY (reviewee_id) REFERENCES users(user_id),
    -- 这里就是添加唯一约束
    UNIQUE KEY uq_task_user_pair (task_id, reviewer_id)
);


-- ! 举报表

CREATE TABLE reports (
    report_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reporter_id BIGINT NOT NULL,
    reported_user_id BIGINT NOT NULL,
    reported_task_id BIGINT,
    reported_review_id BIGINT,
    reason TEXT NOT NULL,
    status ENUM('PENDING', 'RESOLVED', 'REJECTED') DEFAULT 'PENDING',
    reported_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_reporter FOREIGN KEY (reporter_id) REFERENCES users(user_id),
    CONSTRAINT fk_reported_user FOREIGN KEY (reported_user_id) REFERENCES users(user_id),
    CONSTRAINT fk_reported_task FOREIGN KEY (reported_task_id) REFERENCES tasks(task_id),
    CONSTRAINT fk_reported_review FOREIGN KEY (reported_review_id) REFERENCES reviews(review_id)
);

-- ? 字段名	类型建议	含义说明
-- report_id	BIGINT	主键，自增举报 ID
-- reporter_id	BIGINT	外键，发起举报的用户 ID
-- reported_user_id	BIGINT	外键，被举报的用户 ID
-- reported_task_id	BIGINT (可空)	外键，被举报的任务 ID（如果举报的是任务）
-- reported_review_id	BIGINT (可空)	外键，被举报的评论 ID（如果举报的是评价）
-- reason	TEXT	举报理由
-- status	ENUM	举报处理状态（待处理 / 已处理 / 拒绝等）
-- reported_at	DATETIME	举报时间
