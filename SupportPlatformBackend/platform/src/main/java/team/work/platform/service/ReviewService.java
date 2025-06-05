package team.work.platform.service;

import java.util.List;
import team.work.platform.common.Response;
import team.work.platform.dto.ReviewSubmitDTO;
import team.work.platform.dto.ReviewListDTO;

public interface ReviewService {

    // 提交评价
    Response<Object> SubmitReview(ReviewSubmitDTO reviewSubmitDTO);

    // 管理员查看所有评价
    Response<List<ReviewListDTO>> getAllReviews();

    // 管理员删除评价
    Response<Object> deleteReview(Long reviewId);
} 