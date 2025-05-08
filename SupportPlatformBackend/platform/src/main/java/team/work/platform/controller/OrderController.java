package team.work.platform.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import team.work.platform.common.Response;
import team.work.platform.dto.OrderDTO;
import team.work.platform.service.OrdersService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrdersService ordersService;

    

    // 增加任务和接单
    @PostMapping("/add")
    public Response<Object> createTaskAndOrder(@RequestBody OrderDTO orderDTO) {
        //TODO: 完善任务和接单的控制层
        
        return ordersService.CreateTaskAndOrder(orderDTO);
    }
    
}
