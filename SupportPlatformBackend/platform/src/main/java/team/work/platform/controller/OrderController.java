package team.work.platform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import team.work.platform.common.Response;
import team.work.platform.dto.OrderDTO;
import team.work.platform.dto.OrderApplyDTO;
import team.work.platform.dto.OrderCancelDTO;
import team.work.platform.dto.OrderSubmitDTO;
import team.work.platform.dto.TaskDetailsDTO;
import team.work.platform.service.OrdersService;
import team.work.platform.dto.OrderConfirmDTO;
import team.work.platform.dto.OrderStatusUpdateDTO;

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
    @GetMapping("/my_published")
    public Response<List<TaskDetailsDTO>> getMyPublishedOrders() {
        return ordersService.getOrdersByPosterId(new OrderDTO());
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

    // ? 发布者取消任务
    @PostMapping("/cancel")
    public Response<Object> cancelOrder(@RequestBody OrderCancelDTO orderCancelDTO) {
        return ordersService.cancelOrder(orderCancelDTO);
    }

    // ? 修改未接单任务信息
    @PostMapping("/update")
    public Response<Object> updateOrder(@RequestBody OrderStatusUpdateDTO updateDTO) {
        return ordersService.updateOrder(updateDTO);
    }
    // ? 查询用户自己接受的任务
    @GetMapping("/my-received")
    public Response<Object> getMyReceivedOrders() {
        return ordersService.getMyReceivedOrders();
    }

    // ? 查看任务详情
    @PostMapping("/detail")
    public Response<Object> getOrderDetail(@RequestBody OrderConfirmDTO orderConfirmDTO) {
        return ordersService.getOrderDetail(orderConfirmDTO.getOrderId());
    }
}
