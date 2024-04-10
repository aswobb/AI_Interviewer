package com.app.sns.aiproduct.service.impl;

import com.app.sns.aiproduct.mapper.UserMapper;
import com.app.sns.aiproduct.pojo.entity.SnsUser;
import com.app.sns.aiproduct.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public SnsUser getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public List<SnsUser> getAllUsers() {
        return userMapper.selectList(null);
    }

    @Override
    @Transactional
    public SnsUser createUser(SnsUser user) {
        userMapper.insert(user);
        return user;
    }

    @Override
    @Transactional
    public SnsUser updateUser(Long id, SnsUser user) {
        user.setId(id);
        userMapper.updateById(user);
        user.setGmtUpdate(LocalDateTime.now());
        return user;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        SnsUser user = userMapper.selectById(id);
        if (user != null) {
            user.setEnable(1);
            user.setGmtUpdate(LocalDateTime.now());
            userMapper.updateById(user);
        }
    }

    @Override
    @Transactional
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        SnsUser user = userMapper.selectById(userId);
        if (user != null && user.getPassword().equals(oldPassword)) {
            user.setPassword(newPassword);
            int rows = userMapper.updateById(user);
            return rows > 0;
        }
        return false;
    }
}