package team.work.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import team.work.platform.model.Users;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;




@Mapper
public interface UsersMapper extends BaseMapper<Users> {
    // * 如果需要自定义SQL，也可以写方法并写对应的XML

    // 查找用户名
    @Select("SELECT * FROM users where username = #{userName}")
    Users selectByUserName(@Param("userName") String userName);

    // 查找邮箱
    @Select("SELECT * FROM users where email = #{email}")
    Users selectByUserEmail(@Param("email") String email);

    // 通过邮箱查找密码
    @Select("SELECT password FROM users WHERE email = #{email}")
    Users findPasswordByEmail(@Param("email") String email);

    // 创建用户
    @Insert("INSERT INTO users(username,email,password) VALUES(#{userName},#{email},#{password})")
    int createUser(@Param("userName") String userName, @Param("email") String email,
            @Param("password") String password);

    // INSERT INTO users(username,email,password)
    // VALUES("test1","123@123.com","123456");

    // CREATE TABLE users (
    // user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    // username VARCHAR(50) NOT NULL UNIQUE,
    // email VARCHAR(100) NOT NULL UNIQUE,
    // password VARCHAR(255) NOT NULL,
    // credit_score INT DEFAULT 100,
    // status ENUM('ACTIVE', 'FROZEN', 'BANNED') DEFAULT 'ACTIVE',
    // role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    // created_at DATETIME DEFAULT CURRENT_TIMESTAMP
    // );
}