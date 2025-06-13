package team.work.platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import team.work.platform.common.Response;
import team.work.platform.dto.ReportSubmitDTO;
import team.work.platform.service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // * 用户提交举报
    @PostMapping("/submit")
    public Response<Object> submitReport(@RequestBody ReportSubmitDTO reportSubmitDTO) {
        return reportService.submitReport(reportSubmitDTO);
    }
    // * 用户查看自己的举报
    @GetMapping("/my")
    public Response<Object> getMyReports() {
        return reportService.getMyReports();
    }
} 