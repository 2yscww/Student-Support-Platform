package team.work.platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import team.work.platform.common.Response;
import team.work.platform.dto.OrderSubmitDTO;
import team.work.platform.dto.ReviewListDTO;
import team.work.platform.dto.ReviewSubmitDTO;
import team.work.platform.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // * 用户提交评价
    @PostMapping("/submit")
    public Response<Object> SubmitReview(@RequestBody ReviewSubmitDTO reviewSubmitDTO) {
        return reviewService.SubmitReview(reviewSubmitDTO);
    }
    // * 用户获得相应任务的评价
    @PostMapping("/order")
    public Response<List<ReviewListDTO>> getReviewsByOrderId(@RequestBody OrderSubmitDTO orderSubmitDTO) {
        return reviewService.getReviewsByOrderId(orderSubmitDTO.getOrderId());
    }
} 