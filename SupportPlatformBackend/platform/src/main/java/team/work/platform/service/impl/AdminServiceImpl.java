package team.work.platform.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import team.work.platform.common.Response;
import team.work.platform.common.JwtAuthenticationFilter;
import team.work.platform.dto.AdminLoginDTO;
import team.work.platform.dto.LoginUserDTO;
import team.work.platform.dto.UserDetailsDTO;
import team.work.platform.dto.UserStatusDTO;
import team.work.platform.dto.ReportListDTO;
import team.work.platform.mapper.UsersMapper;
import team.work.platform.mapper.ReportsMapper;
import team.work.platform.mapper.TaskMapper;
import team.work.platform.model.Users;
import team.work.platform.model.Reports;
import team.work.platform.model.enumValue.Role;
import team.work.platform.model.enumValue.Status;
import team.work.platform.model.enumValue.ReportStatus;
import team.work.platform.service.AdminService;
import team.work.platform.utils.JwtUtil;
import team.work.platform.utils.UserValidator;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ReportsMapper reportsMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public Response<Object> adminLogin(LoginUserDTO adminLoginDTO) {
        // 1. 验证邮箱是否存在
        if (!userValidator.isEmailExist(adminLoginDTO.getEmail())) {
            return Response.Fail(null, "邮箱或密码错误!");
        }

        // 2. 获取用户信息
        Users user = usersMapper.selectByUserEmail(adminLoginDTO.getEmail());

        // 3. 验证用户是否是管理员
        if (user.getRole() != Role.ADMIN) {
            return Response.Fail(null, "该账号没有管理员权限!");
        }

        // 4. 验证密码
        String userPassword = userValidator.findPasswordUseEmail(adminLoginDTO.getEmail());
        if (!userValidator.loginPasswordEqual(userPassword, adminLoginDTO.getPassword())) {
            return Response.Fail(null, "邮箱或密码错误!");
        }

        // 5. 生成JWT令牌
        String token = jwtUtil.generateToken(user.getEmail(), user.getUserID(), user.getRole().toString());

        // 6. 创建返回数据
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("token", token);
        responseData.put("userId", user.getUserID());
        responseData.put("username", user.getUsername());
        responseData.put("role", user.getRole().toString());

        return Response.Success(responseData, "登录成功!");
    }

    @Override
    public Response<List<UserDetailsDTO>> getAllUsers() {
        try {
            List<UserDetailsDTO> users = usersMapper.getAllUserDetails();
            if (users != null && !users.isEmpty()) {
                return Response.Success(users, "查询成功");
            } else {
                return Response.Success(null, "暂无用户数据");
            }
        } catch (Exception e) {
            return Response.Fail(null, "查询用户列表失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Response<Object> freezeUser(UserStatusDTO userStatusDTO) {
        // 获取当前管理员ID
        Long adminId = JwtAuthenticationFilter.getCurrentUserId();
        if (adminId == null) {
            return Response.Fail(null, "管理员未登录!");
        }

        // 验证当前用户是否为管理员
        Users admin = usersMapper.selectById(adminId);
        if (admin == null || admin.getRole() != Role.ADMIN) {
            return Response.Fail(null, "无管理员权限!");
        }

        // 验证目标用户是否存在
        Users user = usersMapper.selectById(userStatusDTO.getUserId());
        if (user == null) {
            return Response.Fail(null, "用户不存在!");
        }

        // 验证目标用户是否已被冻结或封禁
        if (user.getStatus() == Status.FROZEN || user.getStatus() == Status.BANNED) {
            return Response.Fail(null, "该用户已处于" + user.getStatus().getValue() + "状态!");
        }

        // 不能冻结或封禁管理员账号
        if (user.getRole() == Role.ADMIN) {
            return Response.Fail(null, "不能冻结或封禁管理员账号!");
        }

        try {
            // 更新用户状态
            user.setStatus(userStatusDTO.getStatus());
            int result = usersMapper.updateById(user);
            
            if (result > 0) {
                String statusText = userStatusDTO.getStatus() == Status.FROZEN ? "冻结" : "封禁";
                return Response.Success(null, "用户账号已" + statusText );

            } else {
                return Response.Fail(null, "修改用户状态失败!");
            }
        } catch (Exception e) {
            return Response.Error(null, "修改用户状态失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Response<Object> unfreezeUser(UserStatusDTO userStatusDTO) {
        // 获取当前管理员ID
        Long adminId = JwtAuthenticationFilter.getCurrentUserId();
        if (adminId == null) {
            return Response.Fail(null, "管理员未登录!");
        }

        // 验证当前用户是否为管理员
        Users admin = usersMapper.selectById(adminId);
        if (admin == null || admin.getRole() != Role.ADMIN) {
            return Response.Fail(null, "无管理员权限!");
        }

        // 验证目标用户是否存在
        Users user = usersMapper.selectById(userStatusDTO.getUserId());
        if (user == null) {
            return Response.Fail(null, "用户不存在!");
        }

        // 验证目标用户是否处于非正常状态
        if (user.getStatus() == Status.ACTIVE) {
            return Response.Fail(null, "该用户已处于正常状态!");
        }

        try {
            // 更新用户状态为正常
            user.setStatus(Status.ACTIVE);
            int result = usersMapper.updateById(user);
            
            if (result > 0) {
                String previousStatus = user.getStatus().getValue();
                return Response.Success(null, "用户账号已从" + previousStatus + "状态解封!" );

            } else {
                return Response.Fail(null, "解封用户账号失败!");
            }
        } catch (Exception e) {
            return Response.Error(null, "解封用户账号失败: " + e.getMessage());
        }
    }

    @Override
    public Response<List<ReportListDTO>> getReportList(ReportStatus status) {
        try {
            // 获取举报列表
            List<ReportListDTO> reportList = reportsMapper.getReportList(status);
            
            if (reportList != null && !reportList.isEmpty()) {
                // 补充任务标题信息
                for (ReportListDTO report : reportList) {
                    if (report.getReportedTaskId() != null) {
                        String taskTitle = taskMapper.getTaskTitleById(report.getReportedTaskId());
                        report.setReportedTaskTitle(taskTitle);
                    }
                }
                return Response.Success(reportList, "获取举报列表成功");
            } else {
                return Response.Success(null, "暂无举报记录");
            }
        } catch (Exception e) {
            return Response.Error(null, "获取举报列表失败: " + e.getMessage());
        }
    }


    // 验证状态转换是否合法
    private boolean isValidStatusTransition(ReportStatus currentStatus, ReportStatus newStatus) {
        if (currentStatus == newStatus) {
            return false;
        }

        switch (currentStatus) {
            case PENDING:
                // 待处理 -> 调查中/已处理/已驳回/无效
                return newStatus == ReportStatus.INVESTIGATING || 
                       newStatus == ReportStatus.RESOLVED || 
                       newStatus == ReportStatus.REJECTED ||
                       newStatus == ReportStatus.INVALID;
            case INVESTIGATING:
                // 调查中 -> 已处理/已驳回/无效
                return newStatus == ReportStatus.RESOLVED || 
                       newStatus == ReportStatus.REJECTED ||
                       newStatus == ReportStatus.INVALID;
            default:
                // 其他状态不允许变更
                return false;
        }
    }

    @Override
    public Response<List<ReportListDTO>> getPendingReports() {
        try {
            // 获取待处理的举报列表
            List<ReportListDTO> reportList = reportsMapper.getPendingReports();
            
            if (reportList != null && !reportList.isEmpty()) {
                // 补充任务标题信息
                for (ReportListDTO report : reportList) {
                    if (report.getReportedTaskId() != null) {
                        String taskTitle = taskMapper.getTaskTitleById(report.getReportedTaskId());
                        report.setReportedTaskTitle(taskTitle);
                    }
                }
                return Response.Success(reportList, "获取待处理举报列表成功");
            } else {
                return Response.Success(null, "暂无待处理举报");
            }
        } catch (Exception e) {
            return Response.Error(null, "获取待处理举报列表失败: " + e.getMessage());
        }
    }

    @Override
    public Response<List<ReportListDTO>> getResolvedReports() {
        try {
            // 获取已处理的举报列表（包括已处理、已驳回、无效等状态）
            List<ReportListDTO> reportList = reportsMapper.getResolvedReports();
            
            if (reportList != null && !reportList.isEmpty()) {
                // 补充任务标题信息
                for (ReportListDTO report : reportList) {
                    if (report.getReportedTaskId() != null) {
                        String taskTitle = taskMapper.getTaskTitleById(report.getReportedTaskId());
                        report.setReportedTaskTitle(taskTitle);
                    }
                }
                return Response.Success(reportList, "获取已处理举报列表成功");
            } else {
                return Response.Success(null, "暂无已处理举报");
            }
        } catch (Exception e) {
            return Response.Error(null, "获取已处理举报列表失败: " + e.getMessage());
        }
    }
} 