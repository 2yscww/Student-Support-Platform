package team.work.platform.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import team.work.platform.common.Response;
import team.work.platform.common.JwtAuthenticationFilter;
import team.work.platform.dto.ReportDetailsDTO;
import team.work.platform.dto.ReportSubmitDTO;
import team.work.platform.mapper.OrdersMapper;
import team.work.platform.mapper.ReportsMapper;
import team.work.platform.mapper.ReviewMapper;
import team.work.platform.mapper.UsersMapper;
import team.work.platform.model.Orders;
import team.work.platform.model.Reports;
import team.work.platform.model.Reviews;
import team.work.platform.model.Users;
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
    private ReviewMapper reviewMapper;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private OrdersMapper ordersMapper;

    @Override
    @Transactional
    public Response<Object> submitReport(ReportSubmitDTO reportSubmitDTO) {

        // 获取当前用户ID
        Long currentUserId = JwtAuthenticationFilter.getCurrentUserId();
        if (currentUserId == null) {
            return Response.Fail(null, "用户未登录!");
        }
        
        // 检查用户是否有未处理的举报
        if (reportsMapper.countByReporterIdAndStatus(currentUserId, ReportStatus.PENDING) > 0) {
            return Response.Fail(null, "您有待处理的举报，请等待处理完成后再提交。");
        }

        // 验证举报原因是否为空
        if (reportSubmitDTO.getReason() == null || reportSubmitDTO.getReason().trim().isEmpty()) {
            return Response.Fail(null, "举报原因不能为空!");
        }

        Long reportedUserId = null;

        // 根据举报类型验证被举报对象是否存在
        switch (reportSubmitDTO.getReportType()) {
            case USER:
                reportedUserId = reportSubmitDTO.getReportedUserId();
                if (reportedUserId == null) {
                    return Response.Fail(null, "被举报用户ID不能为空!");
                }
                if (!userValidator.isUserIDExist(reportedUserId)) {
                    return Response.Fail(null, "被举报用户不存在!");
                }
                if (currentUserId.equals(reportedUserId)) {
                    return Response.Fail(null, "不能举报自己!");
                }
                break;
            case TASK:
                if (reportSubmitDTO.getReportedOrderId() == null) {
                    return Response.Fail(null, "被举报任务ID不能为空!");
                }
                Orders order = ordersMapper.selectById(reportSubmitDTO.getReportedOrderId());
                if (order == null) {
                    return Response.Fail(null, "被举报任务不存在!");
                }
                if (currentUserId.equals(order.getPosterId())) {
                    return Response.Fail(null, "不能举报自己发布的任务!");
                }
                reportedUserId = order.getPosterId();
                break;
            case REVIEW:
                if (reportSubmitDTO.getReportedReviewId() == null) {
                    return Response.Fail(null, "被举报评价ID不能为空!");
                }
                Reviews review = reviewMapper.selectByReviewId(reportSubmitDTO.getReportedReviewId());
                if (review == null) {
                    return Response.Fail(null, "被举报评价不存在!");
                }
                if (currentUserId.equals(review.getReviewerId())) {
                    return Response.Fail(null, "不能举报自己发表的评价!");
                }
                reportedUserId = review.getReviewerId();
                break;
            default:
                return Response.Fail(null, "无效的举报类型!");
        }

        // 创建举报记录
        Reports report = new Reports();
        report.setReporterId(currentUserId);
        report.setReportedUserId(reportedUserId);
        report.setReportedTaskId(reportSubmitDTO.getReportedOrderId());
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

    @Override
    public Response<Object> getMyReports() {
        // 获取当前用户ID
        Long currentUserId = JwtAuthenticationFilter.getCurrentUserId();
        if (currentUserId == null) {
            return Response.Fail(null, "用户未登录!");
        }

        try {
            // 获取用户的所有举报记录
            List<Reports> reports = reportsMapper.findByReporterId(currentUserId);
            
            // 将Reports转换为ReportDetailsDTO
            List<ReportDetailsDTO> reportDetailsList = reports.stream().map(report -> {
                ReportDetailsDTO dto = new ReportDetailsDTO();
                dto.setReportId(report.getReportId());
                dto.setReportedUserId(report.getReportedUserId());
                
                if (report.getReportedUserId() != null) {
                    Users reportedUser = usersMapper.selectById(report.getReportedUserId());
                    if (reportedUser != null) {
                        dto.setReportedUsername(reportedUser.getUsername());
                    }
                }

                dto.setReportedTaskId(report.getReportedTaskId());
                dto.setReportedReviewId(report.getReportedReviewId());
                dto.setReason(report.getReason());
                dto.setReportType(report.getReportType());
                dto.setStatus(report.getStatus());
                dto.setReportedAt(report.getReportedAt());
                return dto;
            }).collect(Collectors.toList());

            return Response.Success(reportDetailsList, "获取举报记录成功!");
        } catch (Exception e) {
            return Response.Error(null, "获取举报记录失败: " + e.getMessage());
        }
    }
} 