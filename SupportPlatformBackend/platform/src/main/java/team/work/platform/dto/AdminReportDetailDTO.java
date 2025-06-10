package team.work.platform.dto;

import lombok.Data;

@Data
public class AdminReportDetailDTO {
    private ReportListDTO reportInfo;
    private TaskDetailsDTO reportedTaskDetails;
    private ReviewListDTO reportedReviewDetails;
} 