package team.work.platform.service;

import java.net.ResponseCache;
import java.util.List;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;

import team.work.platform.common.Response;
import team.work.platform.dto.registerUserDTO;
import team.work.platform.model.Users;

public interface UsersService {

    // 查找用户名
    Users selectByUserName(String userName);
    // 查找邮箱
    Users selectByUserEmail(String email);

    // 判断用户名是否存在
    boolean isUserNameExist(String userName);

    // 判断邮箱是否存在
    boolean isEmailExist(String email);

    // 判断密码是否一致
    boolean isPasswordEqual(registerUserDTO registerUserDTO);

    //注册用户
    Response<Object> registerUser(registerUserDTO registerUserDTO);
    
}