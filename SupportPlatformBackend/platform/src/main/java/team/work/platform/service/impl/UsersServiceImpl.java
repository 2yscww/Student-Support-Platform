package team.work.platform.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import team.work.platform.dto.RegisterUserDTO;
import team.work.platform.mapper.UsersMapper;
import team.work.platform.model.Users;
import team.work.platform.service.UsersService;
import team.work.platform.common.Response;

@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    private UsersMapper usersmapper;

    // 查找用户名
    @Override
    public Users selectByUserName(String userName) {
        return usersmapper.selectByUserName(userName);
    }

    // 查找邮箱
    @Override
    public Users selectByUserEmail(String email) {
        return usersmapper.selectByUserEmail(email);
    }

    // 判断用户名是否存在
    @Override
    public boolean isUserNameExist(String userName) {
        Users user = usersmapper.selectByUserName(userName);
        return user != null;
    }

    // 判断邮箱是否存在
    @Override
    public boolean isEmailExist(String email) {
        Users user = usersmapper.selectByUserEmail(email); // 查询邮箱
        return user != null;
    }

    // 判断密码是否一致
    @Override
    public boolean isPasswordEqual(RegisterUserDTO registerUserDTO) {
        return registerUserDTO.getPassword().equals(
                registerUserDTO.getConfirmPassword());

    }

    // ? 注册用户
    // TODO 完善注册逻辑
    @Override
    public Response<Object> registerUser(RegisterUserDTO registerUserDTO) {

        if (isUserNameExist(registerUserDTO.getUsername())) {
            return Response.Fail(null, "用户名重复!");
        }

        if (isEmailExist(registerUserDTO.getEmail())) {
            return Response.Fail(null, "此邮箱已注册过账户!");
        }

        if (!isPasswordEqual(registerUserDTO)) {
            return Response.Fail(null, "两次密码不一致!");
        }

        usersmapper.createUser(registerUserDTO.getUsername(),
                registerUserDTO.getEmail(),
                registerUserDTO.getPassword());

        return Response.Success(null, "注册成功!");
    }

}