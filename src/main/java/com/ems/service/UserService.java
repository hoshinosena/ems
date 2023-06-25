package com.ems.service;

import com.ems.po.User;

import java.util.List;

public interface UserService {
    User findUser(int uid);
    User checkUsername(String username);
    List<User> findUsers(String name);
    List<User> findUsers(double money);
    List<User> findUsers();
    int addUser(User user);
    int updUser(User user);
    int delUser(int uid);
}
