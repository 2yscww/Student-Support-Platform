package team.work.platform.model;

import lombok.Data;
import team.work.platform.model.enumValue.*;

import java.time.LocalDateTime;

@Data
public class Orders {
    private Long orderId;
    private Long taskId;
    private Long posterId;
    private Long receiverId;

    private OrderStatus orderStatus = OrderStatus.APPLIED;
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
}
