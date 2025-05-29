package team.work.platform.service;

import team.work.platform.common.Response;
import team.work.platform.dto.ReportSubmitDTO;

public interface ReportService {
    Response<Object> submitReport(ReportSubmitDTO reportSubmitDTO);
} 