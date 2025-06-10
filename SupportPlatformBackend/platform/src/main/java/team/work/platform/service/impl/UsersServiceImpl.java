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
import team.work.platform.dto.UserProfileDTO;
import team.work.platform.dto.UserUpdateDTO;
import team.work.platform.mapper.UsersMapper;
import team.work.platform.model.Users;
import team.work.platform.service.UsersService;
import team.work.platform.utils.JwtUtil;
import team.work.platform.utils.UserValidator;
import team.work.platform.common.Response;
import team.work.platform.common.JwtAuthenticationFilter;
import org.springframework.data.redis.core.StringRedisTemplate;
import team.work.platform.service.EmailService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    private UsersMapper usersmapper;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Response<Object> sendCode(String email) {
        if (userValidator.isEmailExist(email)) {
            return Response.Fail(null, "此邮箱已注册过账户!");
        }
        String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        redisTemplate.opsForValue().set("register:" + email, code, 15, TimeUnit.MINUTES);
        emailService.sendSimpleMail(email, "注册验证码", "您的验证码是：" + code + "，15分钟内有效。");
        return Response.Success(null, "验证码发送成功");
    }

    // ? 用户注册
    @Override
    public Response<Object> RegisterUser(RegisterUserDTO registerUserDTO) {

        String codeInRedis = redisTemplate.opsForValue().get("register:" + registerUserDTO.getEmail());
        if (codeInRedis == null) {
            return Response.Fail(null, "验证码已过期");
        }
        if (!codeInRedis.equals(registerUserDTO.getCode())) {
            return Response.Fail(null, "验证码错误");
        }

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

        redisTemplate.delete("register:" + registerUserDTO.getEmail());

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

    @Override
    public Response<Object> getUserProfile() {
        Long currentUserId = JwtAuthenticationFilter.getCurrentUserId();
        if (currentUserId == null) {
            return Response.Fail(null, "用户未登录");
        }

        Users user = usersmapper.selectById(currentUserId);
        if (user == null) {
            return Response.Fail(null, "用户不存在");
        }

        UserProfileDTO userProfile = new UserProfileDTO();
        userProfile.setUserId(user.getUserID());
        userProfile.setUsername(user.getUsername());
        userProfile.setEmail(user.getEmail());
        userProfile.setCreditScore(user.getCreditScore());
        userProfile.setStatus(user.getStatus().toString());
        userProfile.setRole(user.getRole().toString());
        userProfile.setCreatedAt(java.util.Date.from(user.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant()));

        return Response.Success(userProfile, "获取用户信息成功");
    }

    @Override
    @Transactional
    public Response<Object> updateUserInfo(UserUpdateDTO userUpdateDTO) {
        Long currentUserId = JwtAuthenticationFilter.getCurrentUserId();
        if (currentUserId == null) {
            return Response.Fail(null, "用户未登录");
        }

        Users currentUser = usersmapper.selectById(currentUserId);
        if (currentUser == null) {
            return Response.Fail(null, "用户不存在");
        }

        // 检查用户名是否已存在（排除当前用户）
        Users userByUsername = usersmapper.selectByUserName(userUpdateDTO.getUsername());
        if (userByUsername != null && !userByUsername.getUserID().equals(currentUserId)) {
            return Response.Fail(null, "用户名已存在");
        }

        try {
            int result = usersmapper.updateUser(currentUserId, userUpdateDTO.getUsername());
            if (result > 0) {
                return Response.Success(null, "用户信息更新成功");
            } else {
                return Response.Fail(null, "用户信息更新失败");
            }
        } catch (Exception e) {
            return Response.Error(null, "用户信息更新失败: " + e.getMessage());
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
