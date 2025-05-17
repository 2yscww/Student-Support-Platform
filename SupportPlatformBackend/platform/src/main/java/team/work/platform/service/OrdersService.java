package team.work.platform.service;

import java.util.List;
import java.util.function.ToDoubleBiFunction;

import team.work.platform.common.Response;
import team.work.platform.dto.OrderDTO;
import team.work.platform.dto.OrderApplyDTO;
import team.work.platform.dto.RegisterUserDTO;
import team.work.platform.dto.TaskDetailsDTO;
import team.work.platform.model.Orders;

public interface OrdersService {

    // ? 查看订单

    Response<Object> ViewOrders();
    
    // ? 创建任务/接单
    Response<Object> CreateTaskAndOrder(OrderDTO orderDTO);

    // ? 获取所有任务详情
    List<TaskDetailsDTO> getAllTaskDetails();

    // ? 查询用户发布的任务
    Response<List<TaskDetailsDTO>> getOrdersByPosterId(OrderDTO orderDTO);

    // ? 申请接单
    Response<Object> applyForOrder(OrderApplyDTO orderApplyDTO);

}
