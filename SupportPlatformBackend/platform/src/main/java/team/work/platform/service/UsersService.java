package team.work.platform.service;

import team.work.platform.common.Response;
import team.work.platform.dto.LoginUserDTO;
import team.work.platform.dto.RegisterUserDTO;


public interface UsersService {

    // 查找用户名
    // Users selectByUserName(String userName);
    // 查找邮箱
    // Users selectByUserEmail(String email);

    // 判断用户名是否存在
    // boolean isUserNameExist(String userName);

    // 判断邮箱是否存在
    // boolean isEmailExist(String email);

    // 判断密码是否一致
    // boolean isPasswordEqual(RegisterUserDTO registerUserDTO);

    // ? 用户注册
    Response<Object> RegisterUser(RegisterUserDTO registerUserDTO);

    // ? 用户登录

    Response<Object> LoginUser(LoginUserDTO loginUserDTO);
    
}