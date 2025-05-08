package team.work.platform.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import team.work.platform.model.Tasks;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface TaskMapper extends BaseMapper<Tasks> {
    // TODO 接下去要完善service层
    // ? 思路是由用户提交的task信息来创建任务，然后再由后端来进行创建接单状态

    // 查询所有任务
    @Select("SELECT * FROM tasks")
    List<Tasks> selectAllTasks();

    // 创建任务
    @Insert("INSERT INTO tasks(title,description,reward) VALUES(#{title},#{description},#{reward})")
    int createdTask(Tasks task);
    // int createdTask(@Param("title") String title, @Param("description") String description,@Param("reward") BigDecimal reward);

    // 删除任务
    @Delete("DELETE FROM tasks WHERE task_id = #{taskID}")
    int deleteTask(@Param("taskID") int taskID);

    

    // delete from tasks where task_id = 2;

    // INSERT INTO tasks(title,description,reward)
    // VALUES("帮我开发网站","我需要一个vue框架开发的前端网站",5000)

    // @Insert("INSERT INTO users(username,email,password)
    // VALUES(#{userName},#{email},#{password})")
    // int createUser(@Param("userName") String userName, @Param("email") String
    // email,
    // @Param("password") String password);
}

// ! 任务表

// CREATE TABLE tasks (
// task_id BIGINT PRIMARY KEY AUTO_INCREMENT,
// title VARCHAR(100) NOT NULL,
// description TEXT,
// created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
// deadline DATETIME,
// reward DECIMAL(10, 2) NOT NULL,
// status ENUM('PENDING', 'IN_PROGRESS', 'SUBMITTED', 'COMPLETED', 'CANCELLED',
// 'EXPIRED') DEFAULT 'PENDING'
// );

// -- ? 枚举值 含义说明
// -- PENDING 待接单
// -- IN_PROGRESS 接单人已确认，进行中
// -- SUBMITTED 接单者提交成果，待确认
// -- COMPLETED 发布者确认完成
// -- CANCELLED 发布者或系统取消
// -- EXPIRED 截止时间已过，无人接单

// -- reward 悬赏金额
