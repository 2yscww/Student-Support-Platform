package team.work.platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import team.work.platform.common.Response;
import team.work.platform.dto.AdminLoginDTO;
import team.work.platform.dto.OrderDTO;
import team.work.platform.service.AdminService;
import team.work.platform.service.OrdersService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;

    @Autowired
    private OrdersService ordersService;

    @PostMapping("/login")
    public Response<Object> adminLogin(@RequestBody AdminLoginDTO adminLoginDTO) {
        return adminService.adminLogin(adminLoginDTO);
    }

    @PostMapping("/orders/add")
    public Response<Object> addTask(@RequestBody OrderDTO orderDTO) {
        return ordersService.CreateTaskAndOrder(orderDTO);
    }
} 