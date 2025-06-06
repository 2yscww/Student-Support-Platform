package team.work.platform.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Result;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import team.work.platform.dto.TaskDetailsDTO;
import team.work.platform.model.Orders;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {



    // 查看订单
    @Select("SELECT * FROM orders")
    List<Orders> selectAllOrders();

    // 创建订单
    @Insert("INSERT INTO orders(task_id,poster_id) VALUES(#{taskID},#{posterID})")
    int createOrder(@Param("taskID") Long taskId, @Param("posterID") Long posterID);

    // 根据订单ID查询接单（订单详情）
    @Select("SELECT * FROM orders WHERE order_id = #{orderID}")
    Orders selectOrderById(@Param("orderID") int orderID);

    // 根据taskId查询订单
    @Select("SELECT * FROM orders WHERE task_id = #{taskID}")
    Orders selectOrderByTaskId(@Param("taskID") Long taskID);

    // 更改接单人
    @Update("UPDATE orders SET receiver_id = #{receiverID} where order_id = #{orderID}")
    int updateReceiverID(@Param("receiverID") int receiverID, @Param("orderID") int orderID);

    // ? 根据发布者查询订单
    List<TaskDetailsDTO> selectOrdersByPosterId(@Param("posterId") Long posterId);

    // 根据接单人查询订单
    @Select("SELECT o.order_id, t.task_id, t.title, t.description, t.reward, t.status as task_status, " +
            "u1.username as poster_username, u2.username as receiver_username, " +
            "o.order_status, o.payment_status " +
            "FROM orders o " +
            "LEFT JOIN tasks t ON o.task_id = t.task_id " +
            "LEFT JOIN users u1 ON o.poster_id = u1.user_id " +
            "LEFT JOIN users u2 ON o.receiver_id = u2.user_id " +
            "WHERE o.receiver_id = #{receiverId} " +
            "ORDER BY o.created_at DESC")
    @Results({
        @Result(property = "orderId", column = "order_id"),
        @Result(property = "taskId", column = "task_id"),
        @Result(property = "title", column = "title"),
        @Result(property = "description", column = "description"),
        @Result(property = "reward", column = "reward"),
        @Result(property = "taskStatus", column = "task_status"),
        @Result(property = "posterUsername", column = "poster_username"),
        @Result(property = "receiverUsername", column = "receiver_username"),
        @Result(property = "orderStatus", column = "order_status"),
        @Result(property = "paymentStatus", column = "payment_status")
    })
    List<TaskDetailsDTO> selectOrdersByReceiverId(@Param("receiverId") int receiverId);

    // ? 更新订单状态
    @Update("UPDATE orders SET order_status = #{orderStatus} WHERE order_id = #{orderID}")
    int updateOrderStatus(@Param("orderStatus") String orderStatus, @Param("orderID") int orderID);

    // 更改支付状态
    @Update("UPDATE orders SET payment_status = #{paymentStatus} WHERE order_id = #{orderID}")
    int updatePaymentStatus(@Param("paymentStatus") String paymentStatus, @Param("orderID") int orderID);

    // 删除订单
    @Delete("DELETE FROM orders WHERE order_id = #{orderID}")
    int deleteOrder(@Param("orderID") int orderID);
    
    // 验证订单ID和任务ID是否匹配
    @Select("SELECT COUNT(*) FROM orders WHERE order_id = #{orderID} AND task_id = #{taskID}")
    int verifyOrderTaskMatch(@Param("orderID") int orderID, @Param("taskID") int taskID);

    // 更新订单确认时间
    @Update("UPDATE orders SET confirmed_at = CURRENT_TIMESTAMP WHERE order_id = #{orderID}")
    int updateConfirmedTime(@Param("orderID") int orderID);

    // 更新关联任务状态
    @Update("UPDATE tasks t " +
           "JOIN orders o ON t.task_id = o.task_id " +
           "SET t.status = #{taskStatus} " +
           "WHERE o.order_id = #{orderID}")
    int updateTaskStatus(@Param("taskStatus") String taskStatus, @Param("orderID") int orderID);

    @Select("SELECT o.*, t.title as task_title, t.description as task_description, " +
            "t.price as task_price, t.deadline as task_deadline, " +
            "u.username as publisher_username " +
            "FROM orders o " +
            "LEFT JOIN tasks t ON o.task_id = t.task_id " +
            "LEFT JOIN users u ON t.publisher_id = u.user_id " +
            "WHERE o.receiver_id = #{receiverId} " +
            "ORDER BY o.created_at DESC")
    @Results({
        @Result(property = "orderId", column = "order_id"),
        @Result(property = "taskId", column = "task_id"),
        @Result(property = "receiverId", column = "receiver_id"),
        @Result(property = "status", column = "status"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "completedAt", column = "completed_at"),
        @Result(property = "taskTitle", column = "task_title"),
        @Result(property = "taskDescription", column = "task_description"),
        @Result(property = "taskPrice", column = "task_price"),
        @Result(property = "taskDeadline", column = "task_deadline"),
        @Result(property = "publisherUsername", column = "publisher_username")
    })
    List<Orders> findByReceiverId(Long receiverId);

    @Select("SELECT o.order_id, o.poster_id, o.receiver_id, o.order_status, o.payment_status, o.created_at, o.confirmed_at, " +
            "t.task_id, t.title, t.description, t.reward, t.status as task_specific_status, t.deadline, " +
            "u1.username as poster_username, u2.username as receiver_username " +
            "FROM orders o " +
            "INNER JOIN tasks t ON o.task_id = t.task_id " +
            "INNER JOIN users u1 ON o.poster_id = u1.user_id " +
            "LEFT JOIN users u2 ON o.receiver_id = u2.user_id " +
            "WHERE o.order_id = #{orderId}")
    @Results({
        @Result(property = "orderId", column = "order_id"),
        @Result(property = "orderStatus", column = "order_status"),
        @Result(property = "paymentStatus", column = "payment_status"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "taskId", column = "task_id"),
        @Result(property = "title", column = "title"),
        @Result(property = "description", column = "description"),
        @Result(property = "reward", column = "reward"),
        @Result(property = "taskStatus", column = "task_specific_status"),
        @Result(property = "posterUsername", column = "poster_username"),
        @Result(property = "receiverUsername", column = "receiver_username"),
        @Result(property = "publisherId", column = "poster_id"),
        @Result(property = "receiverId", column = "receiver_id"),
        @Result(property = "deadline", column = "deadline"),
        @Result(property = "confirmedAt", column = "confirmed_at")
    })
    TaskDetailsDTO getOrderDetail(@Param("orderId") Long orderId);

}

// -- ! 接单表

// CREATE TABLE orders (
// order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
// task_id BIGINT NOT NULL,
// poster_id BIGINT NOT NULL,
// receiver_id BIGINT NOT NULL,
// order_status ENUM('APPLIED', 'ACCEPTED', 'SUBMITTED', 'CONFIRMED',
// 'CANCELLED') DEFAULT 'APPLIED',
// payment_status ENUM('UNPAID', 'PAID') DEFAULT 'UNPAID',
// created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
// confirmed_at DATETIME DEFAULT NULL,

// CONSTRAINT fk_task FOREIGN KEY (task_id) REFERENCES tasks(task_id),
// CONSTRAINT fk_poster FOREIGN KEY (poster_id) REFERENCES users(user_id),
// CONSTRAINT fk_receiver FOREIGN KEY (receiver_id) REFERENCES users(user_id)
// );

// -- ? 接单状态 order_status：

// -- 枚举值 含义说明
// -- APPLIED 接单人提交申请，等待发布者选择
// -- ACCEPTED 发布者已选定接单人
// -- SUBMITTED 接单人已提交成果
// -- CONFIRMED 发布者确认任务完成
// -- CANCELLED 任意一方取消或任务终止

// -- ? 支付状态 payment_status：

// -- 枚举值 含义说明
// -- UNPAID 尚未支付
// -- PAID 发布者已支付报酬
