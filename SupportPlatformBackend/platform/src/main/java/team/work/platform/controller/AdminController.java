package team.work.platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import team.work.platform.common.Response;
import team.work.platform.dto.AdminReportDetailDTO;
import team.work.platform.dto.LoginUserDTO;
import team.work.platform.dto.OrderDTO;
import team.work.platform.dto.OrderCancelDTO;
import team.work.platform.dto.OrderDTO;
import team.work.platform.dto.OrderStatusUpdateDTO;
import team.work.platform.dto.ReportListDTO;
import team.work.platform.dto.ReportStatusUpdateDTO;
import team.work.platform.dto.ReviewDeleteDTO;
import team.work.platform.dto.ReviewListDTO;
import team.work.platform.dto.TaskDetailsDTO;
import team.work.platform.dto.UserDetailsDTO;
import team.work.platform.dto.UserStatusDTO;
import team.work.platform.dto.ReportDetailRequestDTO;
import team.work.platform.service.AdminService;
import team.work.platform.service.OrdersService;
import team.work.platform.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private ReviewService reviewService;


    // * 管理员删除任务
    @DeleteMapping("/orders/delete")
    public Response<Object> deleteOrder(@RequestBody OrderCancelDTO orderCancelDTO) {
        // The frontend sends taskId as orderId, so we use it as taskId here.
        return adminService.deleteTaskAndHandleReports(orderCancelDTO.getOrderId());
    }




    // * 管理员查看所有用户
    @GetMapping("/users/list")
    public Response<List<UserDetailsDTO>> getAllUsers() {
        return adminService.getAllUsers();
    }

    // * 冻结用户账号
    @PostMapping("/users/freeze")
    public Response<Object> freezeUser(@RequestBody UserStatusDTO userStatusDTO) {
        return adminService.freezeUser(userStatusDTO);
    }

    // * 解封用户账号
    @PostMapping("/users/unfreeze")
    public Response<Object> unfreezeUser(@RequestBody UserStatusDTO userStatusDTO) {
        return adminService.unfreezeUser(userStatusDTO);
    }

    // * 管理员查看所有举报列表
    @GetMapping("/reports/list/all")
    public Response<List<ReportListDTO>> getAllReports() {
        return adminService.getReportList(null);
    }


    // * 管理员更新举报状态
    @PutMapping("/reports/update-status")
    public Response<Object> updateReportStatus(@RequestBody ReportStatusUpdateDTO reportStatusUpdateDTO) {
        return adminService.updateReportStatus(reportStatusUpdateDTO);
    }


    // * 管理员删除指定评价
    @DeleteMapping("/reviews/delete")
    public Response<Object> deleteReview(@RequestBody ReviewDeleteDTO reviewDeleteDTO) {
        return adminService.deleteReviewAndHandleReports(reviewDeleteDTO.getReviewId());
    }

    // * 获取管理员个人信息
    @GetMapping("/profile")
    public Response<Object> getAdminProfile() {
        return adminService.getAdminProfile();
    }

    // * 管理员获取举报详情
    @PostMapping("/reports/detail")
    public Response<AdminReportDetailDTO> getReportDetails(@RequestBody ReportDetailRequestDTO requestDTO) {
        return adminService.getReportDetails(requestDTO.getReportId());
    }
} 