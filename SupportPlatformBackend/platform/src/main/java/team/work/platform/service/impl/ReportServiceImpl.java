package team.work.platform.service.impl;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import team.work.platform.common.Response;
import team.work.platform.common.JwtAuthenticationFilter;
import team.work.platform.dto.ReportSubmitDTO;
import team.work.platform.mapper.ReportsMapper;
import team.work.platform.mapper.UsersMapper;
import team.work.platform.mapper.TaskMapper;
import team.work.platform.mapper.ReviewMapper;
import team.work.platform.model.Reports;
import team.work.platform.model.enumValue.ReportStatus;
import team.work.platform.service.ReportService;
import team.work.platform.utils.UserValidator;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportsMapper reportsMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private UserValidator userValidator;

    @Override
    @Transactional
    public Response<Object> submitReport(ReportSubmitDTO reportSubmitDTO) {

        // TODO 需要给用户加上限制，不能让用户可以无限制的提交举报

        // 获取当前用户ID
        Long currentUserId = JwtAuthenticationFilter.getCurrentUserId();
        if (currentUserId == null) {
            return Response.Fail(null, "用户未登录!");
        }

        // 验证举报原因是否为空
        if (reportSubmitDTO.getReason() == null || reportSubmitDTO.getReason().trim().isEmpty()) {
            return Response.Fail(null, "举报原因不能为空!");
        }

        // 根据举报类型验证被举报对象是否存在
        switch (reportSubmitDTO.getReportType()) {
            case USER:
                if (reportSubmitDTO.getReportedUserId() == null) {
                    return Response.Fail(null, "被举报用户ID不能为空!");
                }
                if (!userValidator.isUserIDExist(reportSubmitDTO.getReportedUserId())) {
                    return Response.Fail(null, "被举报用户不存在!");
                }
                if (currentUserId.equals(reportSubmitDTO.getReportedUserId())) {
                    return Response.Fail(null, "不能举报自己!");
                }
                break;
            case TASK:
                if (reportSubmitDTO.getReportedTaskId() == null) {
                    return Response.Fail(null, "被举报任务ID不能为空!");
                }
                if (taskMapper.selectByTaskId(reportSubmitDTO.getReportedTaskId()) == null) {
                    return Response.Fail(null, "被举报任务不存在!");
                }
                break;
            case REVIEW:
                if (reportSubmitDTO.getReportedReviewId() == null) {
                    return Response.Fail(null, "被举报评价ID不能为空!");
                }
                if (reviewMapper.selectByReviewId(reportSubmitDTO.getReportedReviewId()) == null) {
                    return Response.Fail(null, "被举报评价不存在!");
                }
                break;
            default:
                return Response.Fail(null, "无效的举报类型!");
        }

        // 创建举报记录
        Reports report = new Reports();
        report.setReporterId(currentUserId);
        report.setReportedUserId(reportSubmitDTO.getReportedUserId());
        report.setReportedTaskId(reportSubmitDTO.getReportedTaskId());
        report.setReportedReviewId(reportSubmitDTO.getReportedReviewId());
        report.setReason(reportSubmitDTO.getReason().trim());
        report.setReportType(reportSubmitDTO.getReportType());
        report.setStatus(ReportStatus.PENDING);
        report.setReportedAt(LocalDateTime.now());

        try {
            reportsMapper.createReport(report);
            return Response.Success(null, "举报提交成功!");
        } catch (Exception e) {
            return Response.Error(null, "举报提交失败: " + e.getMessage());
        }
    }
} 