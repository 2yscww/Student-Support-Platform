package team.work.platform.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import team.work.platform.common.Response;
import team.work.platform.dto.AdminLoginDTO;
import team.work.platform.mapper.UsersMapper;
import team.work.platform.model.Users;
import team.work.platform.model.enumValue.Role;
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

    @Override
    public Response<Object> adminLogin(AdminLoginDTO adminLoginDTO) {
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
} 