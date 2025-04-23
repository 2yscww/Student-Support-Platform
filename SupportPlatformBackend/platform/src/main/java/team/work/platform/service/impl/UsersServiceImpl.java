package team.work.platform.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import team.work.platform.mapper.UsersMapper;
import team.work.platform.model.Users;
import team.work.platform.service.UsersService;

@Service
public class UsersServiceImpl implements UsersService{

    @Autowired
    private UsersMapper usersMapper;

    @Override
    public List<Users> getAllUsers() {
        // 查询全部
        return usersMapper.selectList(null);
    }

    @Override
    public Users getUserById(Long id) {
        return usersMapper.selectById(id);
    }

    @Override
    public void createUser(Users user) {
        usersMapper.insert(user);
    }
    
}
