package team.work.platform.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Result;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import team.work.platform.model.Reports;
import team.work.platform.model.enumValue.ReportStatus;
import team.work.platform.dto.ReportListDTO;
import java.util.List;

@Mapper
public interface ReportsMapper extends BaseMapper<Reports> {
    
    @Insert("INSERT INTO reports (reporter_id, reported_user_id, reported_task_id, reported_review_id, " +
            "reason, report_type, status, reported_at) " +
            "VALUES (#{reporterId}, #{reportedUserId}, #{reportedTaskId}, #{reportedReviewId}, " +
            "#{reason}, #{reportType}, #{status}, #{reportedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "reportId")
    int createReport(Reports report);

    @Select("SELECT r.*, u.username as reported_username " +
            "FROM reports r " +
            "LEFT JOIN users u ON r.reported_user_id = u.user_id " +
            "WHERE r.reporter_id = #{reporterId} " +
            "ORDER BY r.reported_at DESC")
    @Results({
        @Result(property = "reportId", column = "report_id"),
        @Result(property = "reporterId", column = "reporter_id"),
        @Result(property = "reportedUserId", column = "reported_user_id"),
        @Result(property = "reportedTaskId", column = "reported_task_id"),
        @Result(property = "reportedReviewId", column = "reported_review_id"),
        @Result(property = "reason", column = "reason"),
        @Result(property = "reportType", column = "report_type"),
        @Result(property = "status", column = "status"),
        @Result(property = "reportedAt", column = "reported_at"),
        @Result(property = "reportedUsername", column = "reported_username")
    })
    List<Reports> findByReporterId(Long reporterId);

    @Select({
        "<script>",
        "SELECT r.*, ",
        "u1.username as reporter_username, ",
        "u2.username as reported_username ",
        "FROM reports r ",
        "LEFT JOIN users u1 ON r.reporter_id = u1.user_id ",
        "LEFT JOIN users u2 ON r.reported_user_id = u2.user_id ",
        "<where>",
        "<if test='status != null'>",
        "r.status = #{status}",
        "</if>",
        "</where>",
        "ORDER BY r.reported_at DESC",
        "</script>"
    })
    @Results({
        @Result(property = "reportId", column = "report_id"),
        @Result(property = "reporterId", column = "reporter_id"),
        @Result(property = "reporterUsername", column = "reporter_username"),
        @Result(property = "reportedUserId", column = "reported_user_id"),
        @Result(property = "reportedUsername", column = "reported_username"),
        @Result(property = "reportedTaskId", column = "reported_task_id"),
        @Result(property = "reportedReviewId", column = "reported_review_id"),
        @Result(property = "reason", column = "reason"),
        @Result(property = "reportType", column = "report_type"),
        @Result(property = "status", column = "status"),
        @Result(property = "reportedAt", column = "reported_at")
    })
    List<ReportListDTO> getReportList(ReportStatus status);

    // 获取待处理的举报列表
    @Select("SELECT r.*, " +
           "u1.username as reporter_username, " +
           "u2.username as reported_username " +
           "FROM reports r " +
           "LEFT JOIN users u1 ON r.reporter_id = u1.user_id " +
           "LEFT JOIN users u2 ON r.reported_user_id = u2.user_id " +
           "WHERE r.status = 'PENDING' " +
           "ORDER BY r.reported_at DESC")
    @Results({
        @Result(property = "reportId", column = "report_id"),
        @Result(property = "reporterId", column = "reporter_id"),
        @Result(property = "reporterUsername", column = "reporter_username"),
        @Result(property = "reportedUserId", column = "reported_user_id"),
        @Result(property = "reportedUsername", column = "reported_username"),
        @Result(property = "reportedTaskId", column = "reported_task_id"),
        @Result(property = "reportedReviewId", column = "reported_review_id"),
        @Result(property = "reason", column = "reason"),
        @Result(property = "reportType", column = "report_type"),
        @Result(property = "status", column = "status"),
        @Result(property = "reportedAt", column = "reported_at"),
        @Result(property = "handlingRemark", column = "handling_remark"),
        @Result(property = "handledAt", column = "handled_at"),
        @Result(property = "handledBy", column = "handled_by")
    })
    List<ReportListDTO> getPendingReports();

    // 获取已处理的举报列表（包括已处理、已驳回、无效等状态）
    @Select("SELECT r.*, " +
           "u1.username as reporter_username, " +
           "u2.username as reported_username " +
           "FROM reports r " +
           "LEFT JOIN users u1 ON r.reporter_id = u1.user_id " +
           "LEFT JOIN users u2 ON r.reported_user_id = u2.user_id " +
           "WHERE r.status != 'PENDING' " +
           "ORDER BY r.handled_at DESC, r.reported_at DESC")
    @Results({
        @Result(property = "reportId", column = "report_id"),
        @Result(property = "reporterId", column = "reporter_id"),
        @Result(property = "reporterUsername", column = "reporter_username"),
        @Result(property = "reportedUserId", column = "reported_user_id"),
        @Result(property = "reportedUsername", column = "reported_username"),
        @Result(property = "reportedTaskId", column = "reported_task_id"),
        @Result(property = "reportedReviewId", column = "reported_review_id"),
        @Result(property = "reason", column = "reason"),
        @Result(property = "reportType", column = "report_type"),
        @Result(property = "status", column = "status"),
        @Result(property = "reportedAt", column = "reported_at"),
        @Result(property = "handlingRemark", column = "handling_remark"),
        @Result(property = "handledAt", column = "handled_at"),
        @Result(property = "handledBy", column = "handled_by")
    })
    List<ReportListDTO> getResolvedReports();
} 