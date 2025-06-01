package team.work.platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

import team.work.platform.common.Response;
import team.work.platform.dto.AdminLoginDTO;
import team.work.platform.dto.LoginUserDTO;
import team.work.platform.dto.OrderDTO;
import team.work.platform.dto.OrderCancelDTO;
import team.work.platform.dto.OrderStatusUpdateDTO;
import team.work.platform.dto.TaskDetailsDTO;
import team.work.platform.dto.UserDetailsDTO;
import team.work.platform.service.AdminService;
import team.work.platform.service.OrdersService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;

    @Autowired
    private OrdersService ordersService;

    // ? 管理员登录
    @PostMapping("/login")
    public Response<Object> adminLogin(@RequestBody LoginUserDTO adminLoginDTO) {
        return adminService.adminLogin(adminLoginDTO);
    }

    // ? 管理员添加任务
    @PostMapping("/orders/add")
    public Response<Object> addTask(@RequestBody OrderDTO orderDTO) {
        return ordersService.CreateTaskAndOrder(orderDTO);
    }

    // ? 管理员删除任务
    @DeleteMapping("/orders/delete")
    public Response<Object> deleteOrder(@RequestBody OrderCancelDTO orderCancelDTO) {
        return ordersService.adminDeleteOrderById(orderCancelDTO.getOrderId());
    }

    // ? 管理员查看所有任务
    @GetMapping("/orders/list")
    public Response<List<TaskDetailsDTO>> getAllOrders() {
        List<TaskDetailsDTO> taskDetailsList = ordersService.getAllTaskDetails();
        if (taskDetailsList != null && !taskDetailsList.isEmpty()) {
            return Response.Success(taskDetailsList, "查询成功");
        } else {
            return Response.Success(null, "暂无任务");
        }
    }

    // ? 管理员更新任务状态
    @PutMapping("/orders/updateStatus")
    public Response<Object> updateOrderStatus(@RequestBody OrderStatusUpdateDTO updateDTO) {
        return ordersService.adminUpdateOrderStatus(updateDTO);
    }

    // ? 管理员查看所有用户
    @GetMapping("/users/list")
    public Response<List<UserDetailsDTO>> getAllUsers() {
        return adminService.getAllUsers();
    }
} 