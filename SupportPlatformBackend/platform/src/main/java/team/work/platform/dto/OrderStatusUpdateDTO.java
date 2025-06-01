package team.work.platform.dto;

import lombok.Data;
import team.work.platform.model.enumValue.OrderStatus;
import team.work.platform.model.enumValue.TaskStatus;

@Data
public class OrderStatusUpdateDTO {
    private Long orderId;            // 订单ID
    private String orderStatus;      // 订单状态
    private String taskStatus;       // 任务状态
    private String reason;           // 更新原因（可选）
} 