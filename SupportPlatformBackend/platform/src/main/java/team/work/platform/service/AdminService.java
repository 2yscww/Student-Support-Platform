package team.work.platform.service;

import team.work.platform.common.Response;
import team.work.platform.dto.AdminLoginDTO;

public interface AdminService {
    // 管理员登录
    Response<Object> adminLogin(AdminLoginDTO adminLoginDTO);
} 