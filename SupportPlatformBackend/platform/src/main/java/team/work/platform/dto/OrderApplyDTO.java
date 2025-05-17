package team.work.platform.dto;

import lombok.Data;

@Data
public class OrderApplyDTO {
    private Long orderId;     // 要申请的订单ID
    private Long taskId;     // 要申请的任务ID
    private Long receiverId; // 申请接单的用户ID
} 