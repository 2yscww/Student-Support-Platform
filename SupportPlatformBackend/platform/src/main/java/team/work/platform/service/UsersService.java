package team.work.platform.service;

import java.util.List;

import team.work.platform.model.Users;

public interface UsersService {
    List<Users> getAllUsers();
    Users getUserById(Long id);
    void createUser(Users user);
    
}