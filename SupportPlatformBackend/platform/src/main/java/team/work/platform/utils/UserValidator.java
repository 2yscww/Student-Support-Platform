package team.work.platform.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.work.platform.mapper.UsersMapper;
import team.work.platform.model.Users;

@Component
public class UserValidator {
    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // 创建 logger 实例
    private static final Logger logger = LoggerFactory.getLogger(UserValidator.class);

    // * 验证用户名是否存在
    public boolean isUserNameExist(String username) {
        Users user = usersMapper.selectByUserName(username);
        return user != null;

    }

    // * 通过ID验证用户是否存在

    public boolean isUserIDExist(Long userID) {
        Users user = usersMapper.selectByUserID(userID);

        return user != null;
    }

    // * 验证邮箱是否存在
    public boolean isEmailExist(String email) {
        Users userEmail = usersMapper.selectByUserEmail(email); // 查询邮箱
        return userEmail != null;

    }

    // * 验证两次密码是否一致
    public boolean isPasswordEqual(String password, String confirmPassword) {
        return password.equals(confirmPassword);

    }


    // * 通过邮箱查找密码
    public String findPasswordUseEmail(String email) {
        Users user = usersMapper.findPasswordByEmail(email);

        String userPassword = user.getPassword();

        logger.info("从数据库中查找到的密码:{}",userPassword);

        return userPassword;
    }

    // * 校对密码

    public boolean loginPasswordEqual(String userPassword, String LoginPassword) {
        logger.info("从DTO中获取到的密码:{}",LoginPassword);
        boolean passwordMatch = passwordEncoder.matches(LoginPassword, userPassword);
        return passwordMatch;

    }
}
