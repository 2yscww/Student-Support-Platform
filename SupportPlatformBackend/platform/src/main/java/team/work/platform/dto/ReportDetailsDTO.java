package team.work.platform.dto;

import java.time.LocalDateTime;
import lombok.Data;
import team.work.platform.model.enumValue.ReportType;
import team.work.platform.model.enumValue.ReportStatus;

@Data
public class ReportDetailsDTO {
    private Long reportId;            // 举报ID
    private Long reportedUserId;      // 被举报用户ID
    private String reportedUsername;  // 被举报用户名
    private Long reportedTaskId;      // 被举报任务ID
    private Long reportedReviewId;    // 被举报评价ID
    private String reason;            // 举报原因
    private ReportType reportType;    // 举报类型
    private ReportStatus status;      // 举报状态
    private LocalDateTime reportedAt; // 举报时间
} 