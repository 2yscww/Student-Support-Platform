package team.work.platform.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import team.work.platform.model.Reviews;

@Mapper
public interface ReviewMapper {
    
    // 创建评价
    @Insert("INSERT INTO reviews(task_id, reviewer_id, reviewee_id, rating, content) " +
            "VALUES(#{taskId}, #{reviewerId}, #{revieweeId}, #{rating}, #{content})")
    int createReview(Reviews review);

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
} 