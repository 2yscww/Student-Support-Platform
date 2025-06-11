package team.work.platform.service;

import team.work.platform.common.Response;
import team.work.platform.dto.AdminLoginDTO;
import team.work.platform.dto.LoginUserDTO;
import team.work.platform.dto.UserDetailsDTO;
import team.work.platform.dto.UserStatusDTO;
import team.work.platform.dto.ReportListDTO;
import team.work.platform.dto.ReportStatusUpdateDTO;
import team.work.platform.dto.AdminReportDetailDTO;
import team.work.platform.model.enumValue.ReportStatus;
import java.util.List;

public interface AdminService {
    // 管理员登录
    // Response<Object> adminLogin(LoginUserDTO adminLoginDTO);

    // 获取所有用户信息
    Response<List<UserDetailsDTO>> getAllUsers();

    // 冻结用户账号
    Response<Object> freezeUser(UserStatusDTO userStatusDTO);

    // 解封用户账号
    Response<Object> unfreezeUser(UserStatusDTO userStatusDTO);

    // 获取所有举报列表
    Response<List<ReportListDTO>> getReportList(ReportStatus status);

    // 获取待处理的举报列表
    // Response<List<ReportListDTO>> getPendingReports();

    // 获取已处理的举报列表（包括已处理、已驳回、无效等状态）
    // Response<List<ReportListDTO>> getResolvedReports();

    // 更新举报状态
    Response<Object> updateReportStatus(ReportStatusUpdateDTO reportStatusUpdateDTO);

    // 获取管理员个人信息
    Response<Object> getAdminProfile();

    // 获取举报详情
    Response<AdminReportDetailDTO> getReportDetails(Long reportId);

    Response<Object> deleteReviewAndHandleReports(Long reviewId);

    // 删除任务并处理相关举报
    Response<Object> deleteTaskAndHandleReports(Long taskId);
} 