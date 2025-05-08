package team.work.platform.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderDTO {
    
   // 任务信息
   private String title;
   private String description;
   private BigDecimal reward;

   // 接单信息
   private Long posterId;
   private Long receiverId;

}


// private String email; // 对应 email



// ! --  任务表

// CREATE TABLE tasks (
//     task_id BIGINT PRIMARY KEY AUTO_INCREMENT,
//     title VARCHAR(100) NOT NULL,
//     description TEXT,
//     created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
//     deadline DATETIME,
//     reward DECIMAL(10, 2) NOT NULL, 
//     status ENUM('PENDING', 'IN_PROGRESS', 'SUBMITTED', 'COMPLETED', 'CANCELLED', 'EXPIRED') DEFAULT 'PENDING'
// );

// -- ? 枚举值	含义说明
// --  PENDING	待接单
// --  IN_PROGRESS	接单人已确认，进行中
// --  SUBMITTED	接单者提交成果，待确认
// --  COMPLETED	发布者确认完成
// --  CANCELLED	发布者或系统取消
// --  EXPIRED	截止时间已过，无人接单

// -- reward 悬赏金额


// -- ! 接单表

// CREATE TABLE orders (
//     order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
//     task_id BIGINT NOT NULL,
//     poster_id BIGINT NOT NULL,
//     receiver_id BIGINT ,
//     order_status ENUM('APPLIED', 'ACCEPTED', 'SUBMITTED', 'CONFIRMED', 'CANCELLED') DEFAULT 'APPLIED',
//     payment_status ENUM('UNPAID', 'PAID') DEFAULT 'UNPAID',
//     created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
//     confirmed_at DATETIME DEFAULT NULL,

//     CONSTRAINT fk_task FOREIGN KEY (task_id) REFERENCES tasks(task_id),
//     CONSTRAINT fk_poster FOREIGN KEY (poster_id) REFERENCES users(user_id),
//     CONSTRAINT fk_receiver FOREIGN KEY (receiver_id) REFERENCES users(user_id)
// );


// -- ? 接单状态 order_status：

// -- 枚举值	含义说明
// -- APPLIED	接单人提交申请，等待发布者选择
// -- ACCEPTED	发布者已选定接单人
// -- SUBMITTED	接单人已提交成果
// -- CONFIRMED	发布者确认任务完成
// -- CANCELLED	任意一方取消或任务终止

// -- ? 支付状态 payment_status：

// -- 枚举值	含义说明
// -- UNPAID	尚未支付
// -- PAID	发布者已支付报酬
