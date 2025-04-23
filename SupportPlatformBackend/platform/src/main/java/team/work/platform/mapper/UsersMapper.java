package team.work.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import team.work.platform.model.Users;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UsersMapper extends BaseMapper<Users> {
    // 如果你要自定义SQL，也可以写方法并写对应的XML
}
