package team.work.platform.service;

import team.work.platform.common.Response;
import team.work.platform.dto.AdminLoginDTO;
import team.work.platform.dto.LoginUserDTO;
import team.work.platform.dto.UserDetailsDTO;
import java.util.List;

public interface AdminService {
    // 管理员登录
    Response<Object> adminLogin(LoginUserDTO adminLoginDTO);

    // 获取所有用户信息
    Response<List<UserDetailsDTO>> getAllUsers();
} 