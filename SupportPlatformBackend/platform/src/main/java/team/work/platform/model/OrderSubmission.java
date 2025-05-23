package team.work.platform.model;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;

@Data
@TableName("order_submissions")
public class OrderSubmission {
    @TableId(value = "submission_id", type = IdType.AUTO)
    private Long submissionId;
    
    private Long orderId;
    private String description;
    private LocalDateTime submittedAt;
    private String attachmentUrl;
} 