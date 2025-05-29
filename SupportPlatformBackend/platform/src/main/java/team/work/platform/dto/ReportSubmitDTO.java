package team.work.platform.dto;

import lombok.Data;
import team.work.platform.model.enumValue.ReportType;

@Data
public class ReportSubmitDTO {
    private Long reportedUserId;    // 被举报用户ID（当举报类型为USER时必填）
    private Long reportedTaskId;    // 被举报任务ID（当举报类型为TASK时必填）
    private Long reportedReviewId;  // 被举报评价ID（当举报类型为REVIEW时必填）
    private String reason;          // 举报原因
    private ReportType reportType;  // 举报类型
} 

//  ? 举报类型枚举

// USER

// TASK

// REVIEW