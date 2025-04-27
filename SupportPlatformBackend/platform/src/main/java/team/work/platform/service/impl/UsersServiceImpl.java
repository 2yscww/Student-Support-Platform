package team.work.platform.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import team.work.platform.dto.LoginUserDTO;
import team.work.platform.dto.RegisterUserDTO;
import team.work.platform.mapper.UsersMapper;
import team.work.platform.model.Users;
import team.work.platform.service.UsersService;
import team.work.platform.common.Response;
import team.work.platform.util.UserValidator;

@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    private UsersMapper usersmapper;

    @Autowired
    private UserValidator userValidator;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    // ? 用户注册
    // TODO 完善注册逻辑
    @Override
    public Response<Object> RegisterUser(RegisterUserDTO registerUserDTO) {

        if (userValidator.isUserNameExist(registerUserDTO.getUsername())) {
            return Response.Fail(null, "用户名重复!");
        }

        if (userValidator.isEmailExist(registerUserDTO.getEmail())) {
            return Response.Fail(null, "此邮箱已注册过账户!");
        }

        if (!userValidator.isPasswordEqual(registerUserDTO.getPassword(),registerUserDTO.getConfirmPassword())) {
            return Response.Fail(null, "两次密码不一致!");
        }

        String encodePassword = passwordEncoder.encode(registerUserDTO.getPassword());

        usersmapper.createUser(registerUserDTO.getUsername(),
                registerUserDTO.getEmail(),
                encodePassword
                );

        return Response.Success(null, "注册成功!");
    }

    // ? 用户登录
    @Override
    public Response<Object> LoginUser(LoginUserDTO loginUserDTO) {
        // TODO 完善用户登录逻辑
        
        if (!userValidator.isEmailExist(loginUserDTO.getEmail())) {
            return Response.Fail(null, "邮箱未注册!");

            // TODO 测试完成后应当采用此方式作为返回响应
            // return Response.Fail(null, "邮箱或密码输入错误!");
        }

        String userPassword = userValidator.findPasswordUseEmail(loginUserDTO.getEmail());

        if (!userValidator.loginPasswordEqual(userPassword,loginUserDTO.getPassword())) {
            return Response.Fail(null, "密码错误!");

        }

        
    
        return Response.Success(null, "登录成功!");
    }

}



 // // 查找用户名
    // @Override
    // public Users selectByUserName(String userName) {
    //     return usersmapper.selectByUserName(userName);
    // }

    // // 查找邮箱
    // @Override
    // public Users selectByUserEmail(String email) {
    //     return usersmapper.selectByUserEmail(email);
    // }

    // // 判断用户名是否存在
    // @Override
    // public boolean isUserNameExist(String userName) {
    //     Users user = usersmapper.selectByUserName(userName);
    //     return user != null;
    // }

    // // 判断邮箱是否存在
    // @Override
    // public boolean isEmailExist(String email) {
    //     Users user = usersmapper.selectByUserEmail(email); // 查询邮箱
    //     return user != null;
    // }

    // // 判断密码是否一致
    // @Override
    // public boolean isPasswordEqual(RegisterUserDTO registerUserDTO) {
    //     return registerUserDTO.getPassword().equals(
    //             registerUserDTO.getConfirmPassword());

    // }
