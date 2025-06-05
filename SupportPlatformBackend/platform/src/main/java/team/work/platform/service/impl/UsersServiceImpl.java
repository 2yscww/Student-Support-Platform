package team.work.platform.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import team.work.platform.dto.LoginUserDTO;
import team.work.platform.dto.RegisterUserDTO;
import team.work.platform.dto.PasswordUpdateDTO;
import team.work.platform.mapper.UsersMapper;
import team.work.platform.model.Users;
import team.work.platform.service.UsersService;
import team.work.platform.utils.JwtUtil;
import team.work.platform.utils.UserValidator;
import team.work.platform.common.Response;
import team.work.platform.common.JwtAuthenticationFilter;

@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    private UsersMapper usersmapper;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ? 用户注册

    @Override
    public Response<Object> RegisterUser(RegisterUserDTO registerUserDTO) {

        if (userValidator.isUserNameExist(registerUserDTO.getUsername())) {
            return Response.Fail(null, "用户名重复!");
        }

        if (userValidator.isEmailExist(registerUserDTO.getEmail())) {
            return Response.Fail(null, "此邮箱已注册过账户!");
        }

        if (!userValidator.isPasswordEqual(registerUserDTO.getPassword(), registerUserDTO.getConfirmPassword())) {
            return Response.Fail(null, "两次密码不一致!");
        }

        String encodePassword = passwordEncoder.encode(registerUserDTO.getPassword());

        usersmapper.createUser(registerUserDTO.getUsername(),
                registerUserDTO.getEmail(),
                encodePassword);

        // 获取新注册的用户信息
        Users user = usersmapper.selectByUserEmail(registerUserDTO.getEmail());

        // 生成JWT令牌
        String token = jwtUtil.generateToken(user.getEmail(), user.getUserID(), user.getRole().toString());

        // 创建返回数据对象，包含令牌和用户基本信息
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("token", token);
        responseData.put("userId", user.getUserID());
        responseData.put("username", user.getUsername());
        responseData.put("role", user.getRole().toString());

        return Response.Success(responseData, "注册成功!");
    }

    // ? 用户登录
    @Override
    public Response<Object> LoginUser(LoginUserDTO loginUserDTO) {

        if (!userValidator.isEmailExist(loginUserDTO.getEmail())) {
            // return Response.Fail(null, "邮箱未注册!");

            return Response.Fail(null, "邮箱或密码输入错误!");
        }

        String userPassword = userValidator.findPasswordUseEmail(loginUserDTO.getEmail());

        if (!userValidator.loginPasswordEqual(userPassword, loginUserDTO.getPassword())) {
            return Response.Fail(null, "密码错误!");

        }

        // 获取用户信息
        Users user = usersmapper.selectByUserEmail(loginUserDTO.getEmail());

        // 生成JWT令牌
        String token = jwtUtil.generateToken(user.getEmail(), user.getUserID(), user.getRole().toString());

        // 创建返回数据对象，包含令牌和用户基本信息
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("token", token);
        responseData.put("userId", user.getUserID());
        responseData.put("username", user.getUsername());
        responseData.put("role", user.getRole().toString());
        responseData.put("status", user.getStatus().toString());

        return Response.Success(responseData, "登录成功!");
    }

    @Override
    @Transactional
    public Response<Object> updatePassword(PasswordUpdateDTO passwordUpdateDTO) {
        // 获取当前用户ID
        Long currentUserId = JwtAuthenticationFilter.getCurrentUserId();
        if (currentUserId == null) {
            return Response.Fail(null, "用户未登录");
        }

        // 获取用户信息
        Users user = usersmapper.selectById(currentUserId);
        if (user == null) {
            return Response.Fail(null, "用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(passwordUpdateDTO.getOldPassword(), user.getPassword())) {
            return Response.Fail(null, "旧密码错误");
        }

        // 验证新密码与确认密码是否一致
        if (!passwordUpdateDTO.getNewPassword().equals(passwordUpdateDTO.getConfirmPassword())) {
            return Response.Fail(null, "两次输入的新密码不一致");
        }

        // 验证新密码不能与旧密码相同
        if (passwordUpdateDTO.getOldPassword().equals(passwordUpdateDTO.getNewPassword())) {
            return Response.Fail(null, "新密码不能与旧密码相同");
        }

        try {
            // 加密新密码
            String encodedNewPassword = passwordEncoder.encode(passwordUpdateDTO.getNewPassword());
            
            // 更新密码
            int result = usersmapper.updatePassword(currentUserId, encodedNewPassword);
            if (result > 0) {
                return Response.Success(null, "密码修改成功");
            } else {
                return Response.Fail(null, "密码修改失败");
            }
        } catch (Exception e) {
            return Response.Error(null, "密码修改失败: " + e.getMessage());
        }
    }

}

// // 查找用户名
// @Override
// public Users selectByUserName(String userName) {
// return usersmapper.selectByUserName(userName);
// }

// // 查找邮箱
// @Override
// public Users selectByUserEmail(String email) {
// return usersmapper.selectByUserEmail(email);
// }

// // 判断用户名是否存在
// @Override
// public boolean isUserNameExist(String userName) {
// Users user = usersmapper.selectByUserName(userName);
// return user != null;
// }

// // 判断邮箱是否存在
// @Override
// public boolean isEmailExist(String email) {
// Users user = usersmapper.selectByUserEmail(email); // 查询邮箱
// return user != null;
// }

// // 判断密码是否一致
// @Override
// public boolean isPasswordEqual(RegisterUserDTO registerUserDTO) {
// return registerUserDTO.getPassword().equals(
// registerUserDTO.getConfirmPassword());

// }
