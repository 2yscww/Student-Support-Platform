package team.work.platform.model;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Reviews {
    private Long reviewId;
    private Long taskId;
    private Long reviewerId;
    private Long revieweeId;
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;
}