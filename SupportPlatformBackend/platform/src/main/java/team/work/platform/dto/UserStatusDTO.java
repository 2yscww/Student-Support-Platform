package team.work.platform.dto;

import lombok.Data;
import team.work.platform.model.enumValue.Status;

@Data
public class UserStatusDTO {
    private Long userId;      // 用户ID
    private Status status;    // 用户状态
} 