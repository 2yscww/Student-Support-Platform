package team.work.platform.model;

import team.work.platform.model.enumValue.*;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tasks") // 映射数据库表名
public class Tasks {
    @TableId(value = "task_id", type = IdType.AUTO)
    private Long taskId; // 任务ID

    private String title; 

    private String description; 

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt; 

    private LocalDateTime deadline; // 截止时间

    private BigDecimal reward; 

    private TaskStatus status = TaskStatus.PENDING; // 枚举字段，对应 status
}
