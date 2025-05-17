package team.work.platform.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Service;

import team.work.platform.common.Response;
import team.work.platform.dto.OrderDTO;
import team.work.platform.dto.OrderApplyDTO;
import team.work.platform.dto.TaskDetailsDTO;
import team.work.platform.mapper.OrdersMapper;
import team.work.platform.mapper.TaskMapper;
import team.work.platform.model.Orders;
import team.work.platform.model.Tasks;
import team.work.platform.model.enumValue.OrderStatus;
import team.work.platform.model.enumValue.TaskStatus;
import team.work.platform.service.OrdersService;
import team.work.platform.util.UserValidator;

@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private TaskMapper tasksMapper;

    @Autowired
    private UserValidator userValidator;


    // ? 查看订单
    @Override
    public Response<Object> ViewOrders() {

        // TODO 完善查看任务和订单的功能逻辑

        List ordersList = ordersMapper.selectAllOrders();

        return Response.Success(ordersList, null);
    }

    // poster_id BIGINT NOT NULL,
    // receiver_id BIGINT ,
    // order_status ENUM('APPLIED', 'ACCEPTED', 'SUBMITTED', 'CONFIRMED', 'CANCELLED') DEFAULT 'APPLIED',

    // ? 创建任务和订单
    @Override
    public Response<Object> CreateTaskAndOrder(OrderDTO orderDTO) {

        // * 检查用户ID是否存在
        if (!userValidator.isUserIDExist(orderDTO.getPosterId())) {
            return Response.Fail(null, "用户数据非法!");
        }

        // * 创建任务对象
        Tasks task = new Tasks();
        task.setTitle(orderDTO.getTitle());
        task.setDescription(orderDTO.getDescription());
        task.setReward(orderDTO.getReward());
        task.setStatus(TaskStatus.PENDING);
        task.setCreatedAt(LocalDateTime.now());


        // * 插入任务表
        // tasksMapper.createdTask(task);

        int result = tasksMapper.insert(task); // 使用 MyBatis-Plus 的 insert 方法

        // 任务插入失败
        if (result <= 0 || task.getTaskId() == null) {
            return Response.Fail(null, "任务创建失败!");
        }

        // Long taskId = task.getTaskId();

        Long taskId = task.getTaskId();

        // if (taskId == null) {
        //     return Response.Fail(null, "任务创建失败!");
        // }

        // * 插入订单表
        ordersMapper.createOrder(taskId, orderDTO.getPosterId());

        return Response.Success(null, "任务创建成功!");
    }

    // ? 获取所有任务详情
    public List<TaskDetailsDTO> getAllTaskDetails() {
        return tasksMapper.getAllTaskDetails();
    }

    
    // ? 查询用户发布的任务
    @Override
    public Response<List<TaskDetailsDTO>> getOrdersByPosterId(OrderDTO orderDTO) {
        // 验证用户ID是否存在
        Long posterId = orderDTO.getPosterId();
        if (!userValidator.isUserIDExist(posterId)) {
            return Response.Fail(null, "用户不存在!");
        }
        
        // 查询该用户发布的任务详情
        List<TaskDetailsDTO> ordersList = ordersMapper.selectOrdersByPosterId(posterId);
        
        if (ordersList != null && !ordersList.isEmpty()) {
            return Response.Success(ordersList, "查询成功");
        } else {
            return Response.Success(new ArrayList<>(), "暂无发布的任务");
        }
    }
    
    // ? 申请接单
    @Override
    public Response<Object> applyForOrder(OrderApplyDTO orderApplyDTO) {

        Long orderId = orderApplyDTO.getOrderId();
        Long taskId = orderApplyDTO.getTaskId();
        Long receiverId = orderApplyDTO.getReceiverId();
        
        // 验证用户ID是否存在
        if (!userValidator.isUserIDExist(receiverId)) {
            return Response.Fail(null, "用户不存在!");
        }
        
        // 验证任务是否存在
        Tasks task = tasksMapper.selectById(taskId);
        if (task == null) {
            return Response.Fail(null, "任务不存在!");
        }
        
        // 验证任务状态是否为待接单
        if (task.getStatus() != TaskStatus.PENDING) {
            return Response.Fail(null, "该任务不可申请!");
        }
        
        // 验证订单ID和任务ID是否在同一条记录中
        int matchCount = ordersMapper.verifyOrderTaskMatch(orderId.intValue(), taskId.intValue());
        if (matchCount <= 0) {
            return Response.Fail(null, "订单与任务不匹配!");
        }
        
        // 查询订单信息
        Orders order = ordersMapper.selectById(orderId);
        if (order == null) {
            return Response.Fail(null, "订单信息不存在!");
        }
        
        // 检查申请人是否为任务发布者
        if (order.getPosterId().equals(receiverId)) {
            return Response.Fail(null, "不能申请自己发布的任务!");
        }
        
        // 更新订单信息，设置接单人和订单状态
        int result = ordersMapper.updateReceiverID(receiverId.intValue(), taskId.intValue());
        if (result <= 0) {
            return Response.Fail(null, "申请失败!");
        }
        
        // 更新订单状态为已申请
        ordersMapper.updateOrderStatus(OrderStatus.APPLIED.name(), taskId.intValue());
        
        // 更新任务状态
        task.setStatus(TaskStatus.IN_PROGRESS);
        tasksMapper.updateById(task);
        
        return Response.Success(null, "申请成功，等待发布者确认!");
    }
}

// @Override
// public Response<Object> RegisterUser(RegisterUserDTO registerUserDTO) {

// if (userValidator.isUserNameExist(registerUserDTO.getUsername())) {
// return Response.Fail(null, "用户名重复!");
// }

// if (userValidator.isEmailExist(registerUserDTO.getEmail())) {
// return Response.Fail(null, "此邮箱已注册过账户!");
// }

// if
// (!userValidator.isPasswordEqual(registerUserDTO.getPassword(),registerUserDTO.getConfirmPassword()))
// {
// return Response.Fail(null, "两次密码不一致!");
// }

// String encodePassword =
// passwordEncoder.encode(registerUserDTO.getPassword());

// usersmapper.createUser(registerUserDTO.getUsername(),
// registerUserDTO.getEmail(),
// encodePassword
// );

// return Response.Success(null, "注册成功!");
// }