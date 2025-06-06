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
import team.work.platform.mapper.OrdersMapper;
import team.work.platform.model.Reviews;
import team.work.platform.model.Users;
import team.work.platform.model.Orders;
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
    
    @Autowired
    private OrdersMapper ordersMapper;

    @Override
    @Transactional
    public Response<Object> SubmitReview(ReviewSubmitDTO reviewSubmitDTO) {
        if (reviewSubmitDTO == null) return Response.Fail(null, "评价信息不能为空");
        if (reviewSubmitDTO.getOrderId() == null) return Response.Fail(null, "订单ID不能为空");
        if (reviewSubmitDTO.getRevieweeId() == null) return Response.Fail(null, "被评价者ID不能为空");
        if (reviewSubmitDTO.getRating() == null || reviewSubmitDTO.getRating() < 1 || reviewSubmitDTO.getRating() > 5) return Response.Fail(null, "评分必须在1-5之间");
        if (reviewSubmitDTO.getContent() == null || reviewSubmitDTO.getContent().trim().isEmpty()) return Response.Fail(null, "评价内容不能为空");

        Long currentUserId = JwtAuthenticationFilter.getCurrentUserId();
        if (currentUserId == null) return Response.Fail(null, "未获取到用户信息");

        Orders order = ordersMapper.selectById(reviewSubmitDTO.getOrderId());
        if (order == null) return Response.Fail(null, "订单不存在");
        
        Long taskId = order.getTaskId();

        if (reviewMapper.checkReviewPermission(taskId, currentUserId) == 0) return Response.Fail(null, "您没有权限对此任务进行评价");
        if (reviewMapper.checkRevieweeIdentity(taskId, reviewSubmitDTO.getRevieweeId()) == 0) return Response.Fail(null, "被评价者与任务无关");
        if (reviewMapper.checkReviewExists(taskId, currentUserId) > 0) return Response.Fail(null, "您已经评价过该任务");

        Reviews review = new Reviews();
        review.setTaskId(taskId);
        review.setReviewerId(currentUserId);
        review.setRevieweeId(reviewSubmitDTO.getRevieweeId());
        review.setRating(reviewSubmitDTO.getRating());
        review.setContent(reviewSubmitDTO.getContent().trim());

        int reviewResult = reviewMapper.createReview(review);
        if (reviewResult <= 0) return Response.Fail(null, "评价保存失败");

        return Response.Success(null, "评价成功");
    }

    @Override
    public Response<List<ReviewListDTO>> getReviewsByOrderId(Long orderId) {
        try {
            List<ReviewListDTO> reviews = reviewMapper.getReviewsByOrderId(orderId);
            return Response.Success(reviews, "获取评价列表成功");
        } catch (Exception e) {
            return Response.Error(null, "获取评价列表失败: " + e.getMessage());
        }
    }

    @Override
    public Response<List<ReviewListDTO>> getAllReviews() {
        Long currentUserId = JwtAuthenticationFilter.getCurrentUserId();
        if (currentUserId == null) return Response.Fail(null, "用户未登录");

        Users currentUser = usersMapper.selectById(currentUserId);
        if (currentUser == null || currentUser.getRole() != Role.ADMIN) return Response.Fail(null, "无权限访问");

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
        Long currentUserId = JwtAuthenticationFilter.getCurrentUserId();
        if (currentUserId == null) return Response.Fail(null, "用户未登录");

        Users currentUser = usersMapper.selectById(currentUserId);
        if (currentUser == null || currentUser.getRole() != Role.ADMIN) return Response.Fail(null, "无权限访问");

        Reviews review = reviewMapper.selectByReviewId(reviewId);
        if (review == null) return Response.Fail(null, "评价不存在");

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