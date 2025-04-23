package team.work.platform.model;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.EnumValue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import team.work.platform.model.enumValue.Role;
import team.work.platform.model.enumValue.Status;

@Data
@AllArgsConstructor
@NoArgsConstructor

// 表名映射
@TableName("users")

public class Users {

    @TableId
    private Long userID;

    private String username;  // 对应 username
    private String email;  // 对应 email
    private String password;  // 对应 password

    private Integer creditScore;  // 对应 credit_score

    // 状态字段使用 Enum
    @EnumValue
    private Status status = Status.ACTIVE;  // 默认 ACTIVE

    // 角色字段使用 Enum
    @EnumValue
    private Role role = Role.USER;  // 默认 USER

    private LocalDateTime createdAt;  // 对应 created_at

}
