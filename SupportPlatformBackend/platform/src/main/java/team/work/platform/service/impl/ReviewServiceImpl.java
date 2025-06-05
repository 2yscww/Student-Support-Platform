package team.work.platform.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.work.platform.common.Response;
import team.work.platform.common.JwtAuthenticationFilter;
import team.work.platform.dto.ReviewSubmitDTO;
import team.work.platform.dto.ReviewListDTO;
import team.work.platform.mapper.ReviewMapper;
import team.work.platform.mapper.TaskMapper;
import team.work.platform.mapper.UsersMapper;
import team.work.platform.model.Reviews;
import team.work.platform.model.Users;
import team.work.platform.model.enumValue.Role;
import team.work.platform.service.ReviewService;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private UsersMapper usersMapper;

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

    @Override
    public Response<List<ReviewListDTO>> getAllReviews() {
        // 获取当前用户ID
        Long currentUserId = JwtAuthenticationFilter.getCurrentUserId();
        if (currentUserId == null) {
            return Response.Fail(null, "用户未登录");
        }

        // 检查是否是管理员
        Users currentUser = usersMapper.selectById(currentUserId);
        if (currentUser == null || currentUser.getRole() != Role.ADMIN) {
            return Response.Fail(null, "无权限访问");
        }

        try {
            List<ReviewListDTO> reviews = reviewMapper.getAllReviews();
            if (reviews != null && !reviews.isEmpty()) {
                return Response.Success(reviews, "获取评价列表成功");
            } else {
                return Response.Success(null, "暂无评价记录");
            }
        } catch (Exception e) {
            return Response.Error(null, "获取评价列表失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Response<Object> deleteReview(Long reviewId) {
        // 获取当前用户ID
        Long currentUserId = JwtAuthenticationFilter.getCurrentUserId();
        if (currentUserId == null) {
            return Response.Fail(null, "用户未登录");
        }

        // 检查是否是管理员
        Users currentUser = usersMapper.selectById(currentUserId);
        if (currentUser == null || currentUser.getRole() != Role.ADMIN) {
            return Response.Fail(null, "无权限访问");
        }

        // 检查评价是否存在
        Reviews review = reviewMapper.selectByReviewId(reviewId);
        if (review == null) {
            return Response.Fail(null, "评价不存在");
        }

        try {
            int result = reviewMapper.deleteReview(reviewId);
            if (result > 0) {
                return Response.Success(null, "删除评价成功");
            } else {
                return Response.Fail(null, "删除评价失败");
            }
        } catch (Exception e) {
            return Response.Error(null, "删除评价失败: " + e.getMessage());
        }
    }
}