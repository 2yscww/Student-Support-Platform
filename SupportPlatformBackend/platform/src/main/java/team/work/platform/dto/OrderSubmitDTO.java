package team.work.platform.dto;

import lombok.Data;

@Data
public class OrderSubmitDTO {
    private Long orderId;
    private String description;     // 成果描述
    private String attachmentUrl;   // 成果附件链接
} 