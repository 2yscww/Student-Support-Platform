package team.work.platform.service;

import java.util.List;

import team.work.platform.common.Response;
import team.work.platform.dto.OrderDTO;
import team.work.platform.dto.RegisterUserDTO;
import team.work.platform.model.Orders;

public interface OrdersService {

    // ? 创建任务/接单
    Response<Object> CreateTaskAndOrder(OrderDTO orderDTO);




}
