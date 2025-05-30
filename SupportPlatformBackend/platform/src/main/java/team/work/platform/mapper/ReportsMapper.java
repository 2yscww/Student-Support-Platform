package team.work.platform.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import team.work.platform.model.Reports;

@Mapper
public interface ReportsMapper extends BaseMapper<Reports> {
    
    @Insert("INSERT INTO reports (reporter_id, reported_user_id, reported_task_id, reported_review_id, " +
            "reason, report_type, status, reported_at) " +
            "VALUES (#{reporterId}, #{reportedUserId}, #{reportedTaskId}, #{reportedReviewId}, " +
            "#{reason}, #{reportType}, #{status}, #{reportedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "reportId")
    int createReport(Reports report);
} 