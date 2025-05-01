package team.work.platform.model.enumValue;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum TaskStatus {
    @EnumValue
    PENDING,       // 待接单
    @EnumValue
    IN_PROGRESS,   // 进行中
    @EnumValue
    SUBMITTED,     // 已提交成果
    @EnumValue
    COMPLETED,     // 已完成
    @EnumValue
    CANCELLED,     // 已取消
    @EnumValue
    EXPIRED        // 已过期
}
