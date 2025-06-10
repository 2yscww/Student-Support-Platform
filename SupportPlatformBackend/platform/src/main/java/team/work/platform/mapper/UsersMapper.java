package team.work.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import team.work.platform.model.Users;
import team.work.platform.dto.UserDetailsDTO;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;




@Mapper
public interface UsersMapper extends BaseMapper<Users> {
    // * 如果需要自定义SQL，也可以写方法并写对应的XML

    // 查找用户名
    @Select("SELECT * FROM users where username = #{userName}")
    Users selectByUserName(@Param("userName") String userName);

    // 通过ID查找用户
    @Select("SELECT * FROM users where user_id = #{userID}")
    Users selectByUserID(@Param("userID") Long userID);

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

    // ? 获取所有用户详细信息
    @Select("SELECT " +
           "u.user_id as userId, " +
           "u.username, " +
           "u.email, " +
           "u.credit_score as creditScore, " +
           "u.status, " +
           "u.role, " +
           "u.created_at as createdAt " +
           "FROM users u " +
           "ORDER BY u.created_at DESC")
    List<UserDetailsDTO> getAllUserDetails();

    // 修改用户密码
    @Update("UPDATE users SET password = #{newPassword} WHERE user_id = #{userId}")
    int updatePassword(@Param("userId") Long userId, @Param("newPassword") String newPassword);

    // 更新用户信息
    @Update("UPDATE users SET username = #{username} WHERE user_id = #{userId}")
    int updateUser(@Param("userId") Long userId, @Param("username") String username);
}