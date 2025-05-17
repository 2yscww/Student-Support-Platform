package team.work.platform.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import team.work.platform.dto.TaskDetailsDTO;
import team.work.platform.model.Orders;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

    // TODO 完善订单mapper

    // 查看订单
    @Select("SELECT * FROM orders")
    List<Orders> selectAllOrders();

    // 创建订单
    @Insert("INSERT INTO orders(task_id,poster_id) VALUES(#{taskID},#{posterID})")
    int createOrder(@Param("taskID") Long taskId, @Param("posterID") Long posterID);

    // 根据订单ID查询接单（订单详情）
    @Select("SELECT * FROM orders WHERE order_id = #{orderID}")
    Orders selectOrderById(@Param("orderID") int orderID);

    // 更改接单人
    @Update("UPDATE orders SET receiver_id = #{receiverID} where order_id = #{orderID}")
    int updateReceiverID(@Param("receiverID") int receiverID, @Param("orderID") int orderID);

    // ? 根据发布者查询订单
    List<TaskDetailsDTO> selectOrdersByPosterId(@Param("posterId") Long posterId);

    // 根据接单人查询订单
    @Select("SELECT * FROM orders WHERE receiver_id = #{receiverID}")
    List<Orders> selectOrdersByReceiverId(@Param("receiverID") int receiverID);

    // 更新订单状态
    @Update("UPDATE orders SET order_status = #{orderStatus} WHERE order_id = #{orderID}")
    int updateOrderStatus(@Param("orderStatus") String orderStatus, @Param("orderID") int orderID);

    // 更改支付状态
    @Update("UPDATE orders SET payment_status = #{paymentStatus} WHERE order_id = #{orderID}")
    int updatePaymentStatus(@Param("paymentStatus") String paymentStatus, @Param("orderID") int orderID);

    // 删除订单
    @Delete("DELETE FROM orders WHERE order_id = #{orderID}")
    int deleteOrder(@Param("orderID") int orderID);

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
