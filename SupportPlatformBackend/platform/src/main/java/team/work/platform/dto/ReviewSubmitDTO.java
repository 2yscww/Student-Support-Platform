package team.work.platform.dto;

import lombok.Data;

@Data
public class ReviewSubmitDTO {
    private Long taskId;  //任务id
    private Long revieweeId;  //被评价者的id
    private Integer rating; // 评分等级
    private String content; // 评价内容
}