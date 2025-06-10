package team.work.platform.dto;

import lombok.Data;
import team.work.platform.model.enumValue.ReportStatus;

@Data
public class ReportStatusUpdateDTO {
    private Long reportId;
    private ReportStatus newStatus;
} 