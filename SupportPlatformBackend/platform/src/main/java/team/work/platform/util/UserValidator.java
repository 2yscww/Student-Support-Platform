package team.work.platform.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import team.work.platform.mapper.UsersMapper;

@Component
public class UserValidator {
    @Autowired
    private UsersMapper usersMapper;

    // TODO 增加验证器，把service层的验证重构到这里来

}


//  // 查找用户名
//  @Override
//  public Users selectByUserName(String userName) {
//      return usersmapper.selectByUserName(userName);
//  }

//  // 查找邮箱
//  @Override
//  public Users selectByUserEmail(String email) {
//      return usersmapper.selectByUserEmail(email);
//  }

//  // 判断用户名是否存在
//  @Override
//  public boolean isUserNameExist(String userName) {
//      Users user = usersmapper.selectByUserName(userName);
//      return user != null;
//  }

//  // 判断邮箱是否存在
//  @Override
//  public boolean isEmailExist(String email) {
//      Users user = usersmapper.selectByUserEmail(email); // 查询邮箱
//      return user != null;
//  }

//  // 判断密码是否一致
//  @Override
//  public boolean isPasswordEqual(RegisterUserDTO registerUserDTO) {
//      return registerUserDTO.getPassword().equals(
//              registerUserDTO.getConfirmPassword());

//  }