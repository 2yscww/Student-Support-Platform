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
import team.work.platform.dto.RegisterUserDTO;
import team.work.platform.model.Users;
import team.work.platform.service.UsersService;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/user")

public class UserController {
    @Autowired
    private UsersService usersService;

    // 查找用户名
    // @PostMapping("/username")
    // public Users getUserName(@RequestParam String userName) {
    //     return usersService.selectByUserName(userName);
    // }

    // 查找邮箱
    // @PostMapping("/email")
    // public Users getUserEmail(@RequestParam String email) {
    //     return usersService.selectByUserEmail(email);
    // }
    
    // TODO 上线注册用户功能

    @PostMapping("/register")
    public Response<Object> postMethodName(@RequestBody RegisterUserDTO registerUserDTO) {
        return usersService.registerUser(registerUserDTO);
        
    }
    

}
