package team.work.platform.model;

import lombok.Data;
import team.work.platform.model.enumValue.*;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Orders {
    @TableId(value = "order_id", type = IdType.AUTO)
    private Long orderId;
    
    private Long taskId;
    private Long posterId;
    private Long receiverId;

    private OrderStatus orderStatus = OrderStatus.APPLIED;
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
}
