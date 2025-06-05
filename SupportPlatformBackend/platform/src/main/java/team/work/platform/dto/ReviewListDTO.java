package team.work.platform.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ReviewListDTO {
    private Long reviewId;
    private Long taskId;
    private String taskTitle;
    private Long reviewerId;
    private String reviewerUsername;
    private Long revieweeId;
    private String revieweeUsername;
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;
} 