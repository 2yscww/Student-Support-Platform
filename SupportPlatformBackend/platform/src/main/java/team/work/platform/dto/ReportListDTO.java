package team.work.platform.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;
import team.work.platform.model.enumValue.ReportType;
import team.work.platform.model.enumValue.ReportStatus;

@Data
public class ReportListDTO {
    private Long reportId;            // 举报ID
    private Long reporterId;          // 举报人ID
    private String reporterUsername;  // 举报人用户名
    private Long reportedUserId;      // 被举报用户ID
    private String reportedUsername;  // 被举报用户名
    private Long reportedTaskId;      // 被举报任务ID
    private String reportedTaskTitle; // 被举报任务标题
    private Long reportedReviewId;    // 被举报评价ID
    private String reason;            // 举报原因
    private ReportType reportType;    // 举报类型
    private ReportStatus status;      // 举报状态
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime reportedAt; // 举报时间
} 