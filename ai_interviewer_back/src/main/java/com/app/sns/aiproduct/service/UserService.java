package com.app.sns.aiproduct.service;

import com.app.sns.aiproduct.pojo.entity.SnsUser;

import java.util.List;

public interface UserService {

    SnsUser getUserById(Long id);

    List<SnsUser> getAllUsers();

    SnsUser createUser(SnsUser snsUser);

    SnsUser updateUser(Long id, SnsUser snsUser);

    void deleteUser(Long id);

    boolean updatePassword(Long userId, String oldPassword, String newPassword);
}