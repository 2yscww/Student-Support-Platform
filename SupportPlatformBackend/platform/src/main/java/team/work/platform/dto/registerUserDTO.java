package team.work.platform.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDTO {
    private String username; // 对应 username
    private String email; // 对应 email
    private String password; // 对应 password
    private String confirmPassword; //前端确认密码
    private String code;
}
