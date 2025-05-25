package team.work.platform.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import team.work.platform.common.Response;
import team.work.platform.dto.OrderDTO;
import team.work.platform.dto.OrderApplyDTO;
import team.work.platform.dto.OrderSubmitDTO;
import team.work.platform.dto.TaskDetailsDTO;
import team.work.platform.service.OrdersService;
import team.work.platform.dto.OrderConfirmDTO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrdersService ordersService;


    // ? 增加任务和接单
    @PostMapping("/publish")
    public Response<Object> createTaskAndOrder(@RequestBody OrderDTO orderDTO) {
        
        return ordersService.CreateTaskAndOrder(orderDTO);
    }

    // 获取所有任务详情
    @GetMapping("/list")
    public Response<List<TaskDetailsDTO>> getAllTasks() {
        List<TaskDetailsDTO> taskDetailsList = ordersService.getAllTaskDetails();
        if (taskDetailsList != null && !taskDetailsList.isEmpty()) {
            return Response.Success(taskDetailsList, "查询成功");
        } else {
            return Response.Fail(null, "没有任务");
        }
    }
    
    // ? 查询用户发布的任务
    @PostMapping("/my_published")
    //TODO 修改回GET方法
    public Response<List<TaskDetailsDTO>> getMyPublishedOrders(@RequestBody OrderDTO orderDTO) {
        return ordersService.getOrdersByPosterId(orderDTO);
    }
    
    // ? 申请接单
    @PostMapping("/apply")
    public Response<Object> applyForTask(@RequestBody OrderApplyDTO orderApplyDTO) {
        return ordersService.applyForOrder(orderApplyDTO);
    }

    // ? 提交任务结果
    @PostMapping("/submit")
    public Response<Object> submitOrder(@RequestBody OrderSubmitDTO submitDTO) {
        return ordersService.submitOrder(submitDTO);
    }

    // ? 发布者确认任务完成
    @PostMapping("/confirm")
    public Response<Object> confirmOrder(@RequestBody OrderConfirmDTO orderConfirmDTO) {
        return ordersService.confirmOrder(orderConfirmDTO);
    }
}
