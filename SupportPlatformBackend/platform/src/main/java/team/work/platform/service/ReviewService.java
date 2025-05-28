package team.work.platform.service;

import team.work.platform.common.Response;
import team.work.platform.dto.ReviewSubmitDTO;

public interface ReviewService {

    // ? 提交评价
    Response<Object> SubmitReview(ReviewSubmitDTO reviewSubmitDTO);
} 