package team.work.platform.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import team.work.platform.model.Orders;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
    
}