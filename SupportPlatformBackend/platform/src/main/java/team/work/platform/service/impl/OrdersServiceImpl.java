package team.work.platform.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import team.work.platform.common.Response;
import team.work.platform.common.JwtAuthenticationFilter;
import team.work.platform.dto.OrderDTO;
import team.work.platform.dto.OrderApplyDTO;
import team.work.platform.dto.OrderCancelDTO;
import team.work.platform.dto.OrderConfirmDTO;
import team.work.platform.dto.OrderSubmitDTO;
import team.work.platform.dto.TaskDetailsDTO;
import team.work.platform.dto.OrderStatusUpdateDTO;
import team.work.platform.mapper.OrdersMapper;
import team.work.platform.mapper.TaskMapper;
import team.work.platform.mapper.OrderSubmissionMapper;
import team.work.platform.model.Orders;
import team.work.platform.model.Tasks;
import team.work.platform.model.OrderSubmission;
import team.work.platform.model.enumValue.OrderStatus;
import team.work.platform.model.enumValue.TaskStatus;
import team.work.platform.service.OrdersService;
import team.work.platform.utils.UserValidator;

@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private TaskMapper tasksMapper;

    @Autowired
    private OrderSubmissionMapper submissionMapper;

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
    @Transactional
    public Response<Object> CreateTaskAndOrder(OrderDTO orderDTO) {


        // * 获取当前登录用户ID
        Long currentUserId = JwtAuthenticationFilter.getCurrentUserId();
        if (currentUserId == null) {
            return Response.Fail(null, "未获取到用户信息");
        }

        // * 检查用户ID是否存在
        if (!userValidator.isUserIDExist(currentUserId)) {
            return Response.Fail(null, "用户不存在!");
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
        ordersMapper.createOrder(taskId, currentUserId);

        return Response.Success(null, "任务创建成功!");
    }

    // ? 获取所有任务详情
    public List<TaskDetailsDTO> getAllTaskDetails() {
        return tasksMapper.getAllTaskDetails();
    }

    
    // ? 查询用户发布的任务
    @Override
    public Response<List<TaskDetailsDTO>> getOrdersByPosterId(OrderDTO orderDTO) {
        // 获取当前登录用户ID
        Long currentUserId = JwtAuthenticationFilter.getCurrentUserId();
        if (currentUserId == null) {
            return Response.Fail(null, "未获取到用户信息");
        }


        // 查询该用户发布的任务详情
        List<TaskDetailsDTO> ordersList = ordersMapper.selectOrdersByPosterId(currentUserId);
        
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
        // * 目前是接单者提交申请就直接接受，也就是发布者与接单者 一对一
        ordersMapper.updateOrderStatus(OrderStatus.ACCEPTED.name(), taskId.intValue());
        
        // 更新任务状态
        task.setStatus(TaskStatus.IN_PROGRESS);
        tasksMapper.updateById(task);
        
        return Response.Success(null, "申请成功，等待发布者确认!");
    }


    // ? 提交任务结果
    @Override
    @Transactional
    public Response<Object> submitOrder(OrderSubmitDTO submitDTO) {
        
        // 获取当前登录用户ID
        Long currentUserId = JwtAuthenticationFilter.getCurrentUserId();
        if (currentUserId == null) {
            return Response.Fail(null, "未获取到用户信息");
        }

        // 查询订单信息
        Orders order = ordersMapper.selectOrderById(submitDTO.getOrderId().intValue());
        if (order == null) {
            return Response.Fail(null, "订单不存在");
        }

        // 验证当前用户是否为接单者
        if (!currentUserId.equals(order.getReceiverId())) {
            return Response.Fail(null, "只有接单者可以提交任务结果");
        }

        // 验证订单状态是否为已接受
        if (order.getOrderStatus() != OrderStatus.ACCEPTED) {
            return Response.Fail(null, "当前订单状态不允许提交结果");
        }

        // 保存成果提交信息
        OrderSubmission submission = new OrderSubmission();
        submission.setOrderId(submitDTO.getOrderId());
        submission.setDescription(submitDTO.getDescription());
        submission.setAttachmentUrl(submitDTO.getAttachmentUrl());
        submission.setSubmittedAt(LocalDateTime.now());
        
        int submissionResult = submissionMapper.insert(submission);
        if (submissionResult <= 0) {
            return Response.Fail(null, "保存提交内容失败");
        }

        // 更新订单状态为已提交
        int statusResult = ordersMapper.updateOrderStatus(OrderStatus.SUBMITTED.name(), submitDTO.getOrderId().intValue());
        if (statusResult <= 0) {
            return Response.Fail(null, "更新订单状态失败");
        }

        // 更新确认时间
        int timeResult = ordersMapper.updateConfirmedTime(submitDTO.getOrderId().intValue());
        if (timeResult <= 0) {
            return Response.Fail(null, "更新确认时间失败");
        }

        // 更新任务状态
        int taskResult = ordersMapper.updateTaskStatus(TaskStatus.SUBMITTED.name(), submitDTO.getOrderId().intValue());
        if (taskResult <= 0) {
            return Response.Fail(null, "更新任务状态失败");
        }

        return Response.Success(null, "任务结果提交成功");
    }



    // ? 发布者确认任务完成
    @Override
    @Transactional
    public Response<Object> confirmOrder(OrderConfirmDTO orderConfirmDTO) {
        // 获取当前登录用户ID
        Long currentUserId = JwtAuthenticationFilter.getCurrentUserId();
        if (currentUserId == null) {
            return Response.Fail(null, "未获取到用户信息");
        }

        // 查询订单信息
        Orders order = ordersMapper.selectOrderById(orderConfirmDTO.getOrderId().intValue());
        if (order == null) {
            return Response.Fail(null, "订单不存在");
        }

        // 验证当前用户是否为发布者
        if (!currentUserId.equals(order.getPosterId())) {
            return Response.Fail(null, "只有发布者可以确认任务完成");
        }

        // 验证订单状态是否为已提交
        if (order.getOrderStatus() != OrderStatus.SUBMITTED) {
            return Response.Fail(null, "当前订单状态不允许确认完成");
        }

        try {
            // 更新订单状态为已确认
            int statusResult = ordersMapper.updateOrderStatus(OrderStatus.CONFIRMED.name(), orderConfirmDTO.getOrderId().intValue());
            if (statusResult <= 0) {
                throw new RuntimeException("更新订单状态失败");
            }

            // 更新确认时间
            int timeResult = ordersMapper.updateConfirmedTime(orderConfirmDTO.getOrderId().intValue());
            if (timeResult <= 0) {
                throw new RuntimeException("更新确认时间失败");
            }

            // 更新任务状态为已完成
            int taskResult = ordersMapper.updateTaskStatus(TaskStatus.COMPLETED.name(), orderConfirmDTO.getOrderId().intValue());
            if (taskResult <= 0) {
                throw new RuntimeException("更新任务状态失败");
            }

            // 更新任务的deadline为订单的confirmed_at
            int deadlineResult = tasksMapper.updateTaskDeadline(orderConfirmDTO.getOrderId().intValue());
            if (deadlineResult <= 0) {
                throw new RuntimeException("更新任务完成时间失败");
            }

            return Response.Success(null, "任务已确认完成");
        } catch (Exception e) {
            // 由于使用了@Transactional注解，发生异常时会自动回滚
            return Response.Fail(null, e.getMessage());
        }
    }

    // ? 发布者取消任务
    @Override
    @Transactional
    public Response<Object> cancelOrder(OrderCancelDTO orderCancelDTO) {
        // 获取当前登录用户ID
        Long currentUserId = JwtAuthenticationFilter.getCurrentUserId();
        if (currentUserId == null) {
            return Response.Fail(null, "未获取到用户信息");
        }

        // 查询订单信息
        Orders order = ordersMapper.selectOrderById(orderCancelDTO.getOrderId().intValue());
        if (order == null) {
            return Response.Fail(null, "订单不存在");
        }

        // 验证当前用户是否为发布者
        if (!currentUserId.equals(order.getPosterId())) {
            return Response.Fail(null, "只有发布者可以取消任务");
        }

        // 验证订单状态是否允许取消
        if (order.getOrderStatus() == OrderStatus.CONFIRMED || 
            order.getOrderStatus() == OrderStatus.CANCELLED) {
            return Response.Fail(null, "当前订单状态不允许取消");
        }

        try {
            // 更新订单状态为已取消
            int statusResult = ordersMapper.updateOrderStatus(OrderStatus.CANCELLED.name(), 
                orderCancelDTO.getOrderId().intValue());
            if (statusResult <= 0) {
                throw new RuntimeException("更新订单状态失败");
            }

            // 更新任务状态为已取消
            int taskResult = ordersMapper.updateTaskStatus(TaskStatus.CANCELLED.name(), 
                orderCancelDTO.getOrderId().intValue());
            if (taskResult <= 0) {
                throw new RuntimeException("更新任务状态失败");
            }

            return Response.Success(null, "任务已取消");
        } catch (Exception e) {
            // 由于使用了@Transactional注解，发生异常时会自动回滚
            return Response.Fail(null, e.getMessage());
        }
    }

    // ? 管理员删除订单
    @Override
    @Transactional
    public Response<Object> adminDeleteOrderById(Long orderId) {
        try {
            // 查询订单信息
            Orders order = ordersMapper.selectOrderById(orderId.intValue());
            if (order == null) {
                return Response.Fail(null, "订单不存在");
            }

            // 删除订单
            int orderResult = ordersMapper.deleteOrder(orderId.intValue());
            if (orderResult <= 0) {
                throw new RuntimeException("删除订单失败");
            }

            // 删除关联的任务
            int taskResult = tasksMapper.deleteTask(order.getTaskId().intValue());
            if (taskResult <= 0) {
                throw new RuntimeException("删除任务失败");
            }

            return Response.Success(null, "订单和关联任务已成功删除");
        } catch (Exception e) {
            // 由于使用了@Transactional注解，发生异常时会自动回滚
            return Response.Fail(null, e.getMessage());
        }
    }


    // ? 修改未接单任务信息
    // @Override
    // @Transactional
    // public Response<Object> updateOrder(OrderStatusUpdateDTO updateDTO) {
    //     try {
    //         // 获取当前登录用户ID
    //         Long currentUserId = JwtAuthenticationFilter.getCurrentUserId();
    //         if (currentUserId == null) {
    //             return Response.Fail(null, "未获取到用户信息");
    //         }

    //         // 查询订单信息
    //         Orders order = ordersMapper.selectOrderById(updateDTO.getOrderId().intValue());
    //         if (order == null) {
    //             return Response.Fail(null, "订单不存在");
    //         }

    //         // 验证当前用户是否为发布者
    //         if (!currentUserId.equals(order.getPosterId())) {
    //             return Response.Fail(null, "只有发布者可以修改任务信息");
    //         }

    //         // 查询任务信息
    //         Tasks task = tasksMapper.selectById(order.getTaskId());
    //         if (task == null) {
    //             return Response.Fail(null, "任务不存在");
    //         }

    //         // 验证任务状态是否为待接单
    //         if (task.getStatus() != TaskStatus.PENDING) {
    //             return Response.Fail(null, "只能修改未接单的任务");
    //         }

    //         // 更新任务信息
    //         if (updateDTO.getTitle() != null) {
    //             task.setTitle(updateDTO.getTitle());
    //         }
    //         if (updateDTO.getDescription() != null) {
    //             task.setDescription(updateDTO.getDescription());
    //         }
    //         if (updateDTO.getReward() != null) {
    //             task.setReward(updateDTO.getReward());
    //         }

    //         // 保存更新
    //         int result = tasksMapper.updateById(task);
    //         if (result <= 0) {
    //             throw new RuntimeException("更新任务信息失败");
    //         }

    //         return Response.Success(null, "任务信息更新成功" + 
    //             (updateDTO.getReason() != null ? "，原因：" + updateDTO.getReason() : ""));
    //     } catch (Exception e) {
    //         return Response.Fail(null, "更新失败：" + e.getMessage());
    //     }
    // }

    @Override
    public Response<Object> getMyReceivedOrders() {
        // 获取当前用户ID
        Long currentUserId = JwtAuthenticationFilter.getCurrentUserId();
        if (currentUserId == null) {
            return Response.Fail(null, "用户未登录!");
        }

        try {
            // 获取用户接受的所有任务详情
            List<TaskDetailsDTO> taskDetailsList = ordersMapper.selectOrdersByReceiverId(currentUserId.intValue());
            
            if (taskDetailsList != null && !taskDetailsList.isEmpty()) {
                return Response.Success(taskDetailsList, "获取已接受的任务列表成功!");
            } else {
                return Response.Success(new ArrayList<>(), "暂无接受的任务");
            }
        } catch (Exception e) {
            return Response.Error(null, "获取已接受的任务列表失败: " + e.getMessage());
        }
    }

    @Override
    public Response<Object> getOrderDetail(Long orderId) {
        // 获取当前用户ID
        Long currentUserId = JwtAuthenticationFilter.getCurrentUserId();
        if (currentUserId == null) {
            return Response.Fail(null, "用户未登录!");
        }

        try {
            // 获取任务详情
            TaskDetailsDTO taskDetails = ordersMapper.getOrderDetail(orderId);
            
            if (taskDetails == null) {
                return Response.Fail(null, "任务不存在!");
            }

            // 验证当前用户是否有权限查看该任务
            // 只有任务的发布者或接收者可以查看详情
            Orders order = ordersMapper.selectById(orderId);
            if (order == null) {
                return Response.Fail(null, "订单不存在!");
            }


            return Response.Success(taskDetails, "获取任务详情成功!");
        } catch (Exception e) {
            return Response.Error(null, "获取任务详情失败: " + e.getMessage());
        }
    }
}

