package team.work.platform.model;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import team.work.platform.model.enumValue.ReportType;
import team.work.platform.model.enumValue.ReportStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("reports")
public class Reports {
    
    @TableId(value = "report_id")
    private Long reportId;
    
    private Long reporterId;        // 举报人ID
    private Long reportedUserId;    // 被举报人ID
    private Long reportedTaskId;    // 被举报的任务ID（如果有）
    private Long reportedReviewId;  // 被举报的评价ID（如果有）
    private String reason;          // 举报原因

    @EnumValue
    private ReportType reportType;  // 举报类型（USER/TASK/REVIEW）

    @EnumValue
    private ReportStatus status = ReportStatus.PENDING;  // 举报状态，默认为待处理

    private LocalDateTime reportedAt;  // 举报时间

    @TableField(exist = false)
    private String reportedUsername;  // 被举报用户名（非数据库字段）
} 