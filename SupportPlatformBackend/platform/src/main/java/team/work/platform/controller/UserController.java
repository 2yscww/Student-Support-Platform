package team.work.platform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import team.work.platform.common.Response;
import team.work.platform.dto.LoginUserDTO;
import team.work.platform.dto.RegisterUserDTO;
import team.work.platform.dto.PasswordUpdateDTO;
import team.work.platform.dto.UserUpdateDTO;
import team.work.platform.model.Users;
import team.work.platform.service.UsersService;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/user")

public class UserController {
    @Autowired
    private UsersService usersService;


    
    // * 发送注册验证码
    @PostMapping("/send-code")
    public Response<Object> sendCode(@RequestParam String email) {
        return usersService.sendCode(email);
    }


    // * 用户注册
    @PostMapping("/register")
    public Response<Object> registerUser(@RequestBody RegisterUserDTO registerUserDTO) {
        return usersService.RegisterUser(registerUserDTO);
        
    }

    // * 用户登录
    @PostMapping("/login")
    public Response<Object> userLogin(@RequestBody LoginUserDTO loginUserDTO) {
        return usersService.LoginUser(loginUserDTO);
        
    }

    // * 修改密码
    @PostMapping("/password/update")
    public Response<Object> updatePassword(@RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        return usersService.updatePassword(passwordUpdateDTO);
    }

    // * 获取用户个人信息
    @GetMapping("/profile")
    public Response<Object> getUserProfile() {
        return usersService.getUserProfile();
    }

    // * 修改用户个人信息
    @PostMapping("/update")
    public Response<Object> updateUser(@RequestBody UserUpdateDTO userUpdateDTO) {
        return usersService.updateUserInfo(userUpdateDTO);
    }
}
