package team.work.platform.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
// TaskDetailsDTO.java
public class TaskDetailsDTO {
    private Long taskId;
    private String title;
    private String description;
    private BigDecimal reward;
    private String taskStatus;

    private String posterUsername;
    private String receiverUsername;

    private String orderStatus;
    private String paymentStatus;

    // Getter & Setter 略
}
