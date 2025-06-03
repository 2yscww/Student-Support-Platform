package team.work.platform.dto;

import lombok.Data;
import team.work.platform.model.enumValue.OrderStatus;
import team.work.platform.model.enumValue.TaskStatus;
import java.math.BigDecimal;

@Data
public class OrderStatusUpdateDTO {
    private Long orderId;            // 订单ID
    private String orderStatus;      // 订单状态
    private String taskStatus;       // 任务状态
    private String reason;           // 更新原因（可选）
    
    // 任务信息字段
    private String title;           // 任务标题
    private String description;     // 任务描述
    private BigDecimal reward;     // 任务悬赏金额
} 