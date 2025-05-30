package team.work.platform.dto;

import lombok.Data;

@Data
public class AdminLoginDTO {
    private String email;    // 管理员邮箱
    private String password; // 管理员密码
} 