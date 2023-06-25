package com.ems.service.impl;

import com.ems.cache.Info;
import com.ems.cache.TokenPool;
import com.ems.cache.UserCache;
import com.ems.mapper.UserMapper;
import com.ems.po.User;
import com.ems.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserCache userCache;
    @Autowired
    TokenPool tokenPool;
    @Autowired
    UserMapper userMapper;

    @Override
    public User findUser(int uid) {
        User user;
        synchronized (this) {
            if (userCache.cache.containsKey(uid)) {
                return userCache.cache.get(uid);
            }
            user = userMapper.selectUserByUid(uid);
            if (user != null) {
                userCache.cache.put(uid, user);
            }
        }
        return user;
    }

    @Override
    public User checkUsername(String username) {
        return userMapper.checkUsername(username);
    }

    @Override
    public List<User> findUsers(String name) {
        return userMapper.selectUserByName(name);
    }

    @Override
    public List<User> findUsers(double money) {
        return userMapper.selectUserByMoney(money);
    }
    @Override
    public List<User> findUsers() {
        return userMapper.selectUsers();
    }

    @Override
    public int addUser(User user) {
        if (user.getUsername() == null
                || "".equals(user.getUsername())
                || userMapper.checkUsername(user.getUsername()) != null) {
            return 0;
        }
        if (user.getPassword() == null
                || "".equals(user.getPassword())) {
            return 0;
        }
        if (user.getName() == null
                || "".equals(user.getName())) {
            return 0;
        }
        if (user.getTel() == null)
            user.setTel("");
        if (user.getAddress() == null)
            user.setAddress("");
        if (user.getInfo() == null)
            user.setInfo("");
        return userMapper.insertUser(user);
    }

    @Override
    public int updUser(User user) {
        if (user.getPassword() != null
                && "".equals(user.getPassword())) {
            return 0;
        }
        if (user.getName() != null
                && "".equals(user.getName())) {
            return 0;
        }
        if (userMapper.updateUser(user) == 1) {
            // 删除前必缓存userCache不为null
            userCache.cache.remove(user.getUid());
            if (tokenPool.bind.containsKey(user.getUid())) {
                tokenPool.pool.put(tokenPool.bind.get(user.getUid()), new Info(findUser(user.getUid())));
            }
            return 1;
        }
        return 0;
    }

    @Override
    public int delUser(int uid) {
        if (userMapper.deleteUser(uid) == 1) {
            // 删除前必缓存userCache不为null
            userCache.cache.remove(uid);
            if (tokenPool.bind.containsKey(uid)) {
                tokenPool.bind.remove(uid);
                tokenPool.pool.remove(tokenPool.bind.get(uid));
            }
            return 1;
        }
        return 0;
    }
}
