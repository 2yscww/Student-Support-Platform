package team.work.platform.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserDetailsDTO {
    private Long userId;              // 用户ID
    private String username;          // 用户名
    private String email;             // 邮箱
    private Integer creditScore;      // 信用分数
    private String status;            // 状态（ACTIVE/FROZEN/BANNED）
    private String role;              // 角色（USER/ADMIN）
    private LocalDateTime createdAt;  // 创建时间
} 