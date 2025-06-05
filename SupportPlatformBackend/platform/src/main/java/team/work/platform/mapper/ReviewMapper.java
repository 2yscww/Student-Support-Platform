package team.work.platform.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Result;

import java.util.List;
import team.work.platform.model.Reviews;
import team.work.platform.dto.ReviewListDTO;

@Mapper
public interface ReviewMapper {
    
    // 创建评价
    @Insert("INSERT INTO reviews(task_id, reviewer_id, reviewee_id, rating, content) " +
            "VALUES(#{taskId}, #{reviewerId}, #{revieweeId}, #{rating}, #{content})")
    int createReview(Reviews review);

    // 根据ID查询评价
    @Select("SELECT * FROM reviews WHERE review_id = #{reviewId}")
    Reviews selectByReviewId(@Param("reviewId") Long reviewId);

    // 检查是否已经评价过
    @Select("SELECT COUNT(*) FROM reviews " +
            "WHERE task_id = #{taskId} AND reviewer_id = #{reviewerId}")
    int checkReviewExists(@Param("taskId") Long taskId, @Param("reviewerId") Long reviewerId);

    // 检查评价权限（通过订单信息）
    @Select("SELECT COUNT(*) FROM orders " +
            "WHERE task_id = #{taskId} " +
            "AND order_status = 'CONFIRMED' " +
            "AND (poster_id = #{userId} OR receiver_id = #{userId})")
    int checkReviewPermission(@Param("taskId") Long taskId, @Param("userId") Long userId);

    // 检查被评价者身份
    @Select("SELECT COUNT(*) FROM orders " +
            "WHERE task_id = #{taskId} " +
            "AND (poster_id = #{revieweeId} OR receiver_id = #{revieweeId})")
    int checkRevieweeIdentity(@Param("taskId") Long taskId, @Param("revieweeId") Long revieweeId);

    // 管理员查看所有评价列表
    @Select("SELECT r.*, " +
            "u1.username as reviewer_username, " +
            "u2.username as reviewee_username, " +
            "t.title as task_title " +
            "FROM reviews r " +
            "LEFT JOIN users u1 ON r.reviewer_id = u1.user_id " +
            "LEFT JOIN users u2 ON r.reviewee_id = u2.user_id " +
            "LEFT JOIN tasks t ON r.task_id = t.task_id " +
            "ORDER BY r.created_at DESC")
    @Results({
        @Result(property = "reviewId", column = "review_id"),
        @Result(property = "taskId", column = "task_id"),
        @Result(property = "taskTitle", column = "task_title"),
        @Result(property = "reviewerId", column = "reviewer_id"),
        @Result(property = "reviewerUsername", column = "reviewer_username"),
        @Result(property = "revieweeId", column = "reviewee_id"),
        @Result(property = "revieweeUsername", column = "reviewee_username"),
        @Result(property = "rating", column = "rating"),
        @Result(property = "content", column = "content"),
        @Result(property = "createdAt", column = "created_at")
    })
    List<ReviewListDTO> getAllReviews();

    // 删除评价
    @Delete("DELETE FROM reviews WHERE review_id = #{reviewId}")
    int deleteReview(@Param("reviewId") Long reviewId);
} 