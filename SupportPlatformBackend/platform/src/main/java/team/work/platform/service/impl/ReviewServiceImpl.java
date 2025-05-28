package team.work.platform.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.work.platform.common.Response;
import team.work.platform.common.JwtAuthenticationFilter;
import team.work.platform.dto.ReviewSubmitDTO;
import team.work.platform.mapper.ReviewMapper;
import team.work.platform.mapper.TaskMapper;
import team.work.platform.model.Reviews;
import team.work.platform.service.ReviewService;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Override
    @Transactional
    public Response<Object> SubmitReview(ReviewSubmitDTO reviewSubmitDTO) {

        // TODO 把校验转移到别的文件去
        // 参数校验
        if (reviewSubmitDTO == null) {
            return Response.Fail(null, "评价信息不能为空");
        }
        if (reviewSubmitDTO.getTaskId() == null) {
            return Response.Fail(null, "任务ID不能为空");
        }
        if (reviewSubmitDTO.getRevieweeId() == null) {
            return Response.Fail(null, "被评价者ID不能为空");
        }
        if (reviewSubmitDTO.getRating() == null || reviewSubmitDTO.getRating() < 1 || reviewSubmitDTO.getRating() > 5) {
            return Response.Fail(null, "评分必须在1-5之间");
        }
        if (reviewSubmitDTO.getContent() == null || reviewSubmitDTO.getContent().trim().isEmpty()) {
            return Response.Fail(null, "评价内容不能为空");
        }

        // 获取当前登录用户ID
        Long currentUserId = JwtAuthenticationFilter.getCurrentUserId();
        if (currentUserId == null) {
            return Response.Fail(null, "未获取到用户信息");
        }

        // 检查任务是否存在
        if (taskMapper.selectById(reviewSubmitDTO.getTaskId()) == null) {
            return Response.Fail(null, "任务不存在");
        }

        // 检查是否有权限评价
        if (reviewMapper.checkReviewPermission(reviewSubmitDTO.getTaskId(), currentUserId) == 0) {
            return Response.Fail(null, "您没有权限对此任务进行评价");
        }

        // 检查被评价者身份
        if (reviewMapper.checkRevieweeIdentity(reviewSubmitDTO.getTaskId(), reviewSubmitDTO.getRevieweeId()) == 0) {
            return Response.Fail(null, "被评价者与任务无关");
        }

        // 检查是否已经评价过
        if (reviewMapper.checkReviewExists(reviewSubmitDTO.getTaskId(), currentUserId) > 0) {
            return Response.Fail(null, "您已经评价过该任务");
        }

        // 创建评价
        Reviews review = new Reviews();
        review.setTaskId(reviewSubmitDTO.getTaskId());
        review.setReviewerId(currentUserId);
        review.setRevieweeId(reviewSubmitDTO.getRevieweeId());
        review.setRating(reviewSubmitDTO.getRating());
        review.setContent(reviewSubmitDTO.getContent().trim());

        // 保存评价并检查结果
        int reviewResult = reviewMapper.createReview(review);
        if (reviewResult <= 0) {
            return Response.Fail(null, "评价保存失败");
        }

        return Response.Success(null, "评价成功");
    }
}