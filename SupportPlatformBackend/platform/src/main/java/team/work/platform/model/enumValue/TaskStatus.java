package team.work.platform.model.enumValue;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum TaskStatus {
    // @EnumValue
    // PENDING,       // 待接单
    // @EnumValue
    // IN_PROGRESS,   // 进行中
    // @EnumValue
    // SUBMITTED,     // 已提交成果
    // @EnumValue
    // COMPLETED,     // 已完成
    // @EnumValue
    // CANCELLED,     // 已取消
    // @EnumValue
    // EXPIRED        // 已过期


    PENDING("PENDING"),          // 数据库存储 "pending"
    IN_PROGRESS("IN_PROGRESS"),  // 数据库存储 "in_progress"
    SUBMITTED("SUBMITTED"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED"),
    EXPIRED("EXPIRED");

    @EnumValue  // 标记此字段的值存入数据库
    private final String code;

    TaskStatus(String code) {
        this.code = code;
    }
}
