package team.work.platform.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import team.work.platform.common.Response;
import team.work.platform.dto.RegisterUserDTO;
import team.work.platform.mapper.UsersMapper;
import team.work.platform.util.UserValidator;

@ExtendWith(MockitoExtension.class) // 告诉Junit用Mockito的能力
public class UsersServiceImplTest {

    @Mock
    private UsersMapper usersMapper;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private UsersServiceImpl usersServiceImpl;

    private RegisterUserDTO registerUserDTO;

    @BeforeEach
    void setUp() {
        registerUserDTO = new RegisterUserDTO();
        registerUserDTO.setUsername("testuser");
        registerUserDTO.setEmail("test@example.com");
        registerUserDTO.setPassword("123456");
        registerUserDTO.setConfirmPassword("123456");
    }

    @Test
    void registerUser_whenAllValid_thenSuccess() {
        // 假设验证器通过
        when(userValidator.isUserNameExist("testuser")).thenReturn(false);
        when(userValidator.isEmailExist("test@example.com")).thenReturn(false);
        when(userValidator.isPasswordEqual("123456", "123456")).thenReturn(true);

        Response<Object> response = usersServiceImpl.RegisterUser(registerUserDTO);

        assertEquals(200, response.getCode());
        assertEquals("注册成功!", response.getMsg());

        verify(usersMapper, times(1)).createUser(anyString(), anyString(), anyString());
    }

    @Test
    void registerUser_whenUsernameExist_thenFail() {
        when(userValidator.isUserNameExist("testuser")).thenReturn(true);

        Response<Object> response = usersServiceImpl.RegisterUser(registerUserDTO);

        assertEquals(400, response.getCode());
        assertEquals("用户名重复!", response.getMsg());

        verify(usersMapper, times(0)).createUser(anyString(), anyString(), anyString());
    }

    @Test
    void registerUser_whenEmailExist_thenFail() {
        when(userValidator.isUserNameExist("testuser")).thenReturn(false);
        when(userValidator.isEmailExist("test@example.com")).thenReturn(true);

        Response<Object> response = usersServiceImpl.RegisterUser(registerUserDTO);

        assertEquals(400, response.getCode());
        assertEquals("此邮箱已注册过账户!", response.getMsg());

        verify(usersMapper, times(0)).createUser(anyString(), anyString(), anyString());
    }

    @Test
    void registerUser_whenPasswordMismatch_thenFail() {
        when(userValidator.isUserNameExist("testuser")).thenReturn(false);
        when(userValidator.isEmailExist("test@example.com")).thenReturn(false);
        when(userValidator.isPasswordEqual("123456", "123456")).thenReturn(false);

        Response<Object> response = usersServiceImpl.RegisterUser(registerUserDTO);

        assertEquals(400, response.getCode());
        assertEquals("两次密码不一致!", response.getMsg());

        verify(usersMapper, times(0)).createUser(anyString(), anyString(), anyString());
    }
}
