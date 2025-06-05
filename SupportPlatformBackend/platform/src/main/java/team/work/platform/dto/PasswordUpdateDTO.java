package team.work.platform.dto;

import lombok.Data;

@Data
public class PasswordUpdateDTO {
    private String oldPassword;     // 旧密码
    private String newPassword;     // 新密码
    private String confirmPassword; // 确认新密码
} 