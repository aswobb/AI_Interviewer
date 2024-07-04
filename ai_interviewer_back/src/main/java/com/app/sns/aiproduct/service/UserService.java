package com.app.sns.aiproduct.service;

import com.app.sns.aiproduct.pojo.entity.SnsUser;
import com.app.sns.aiproduct.pojo.vo.SnsUserVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserService extends IService<SnsUser> {

    SnsUser getUserById(Long id);

    List<SnsUser> getAllUsers();

    SnsUser createUser(Long creator,SnsUserVO userVO);

    SnsUser updateUser(Long creator,SnsUserVO userVO);

    int deleteUser(@Param("ID") Long id);


    boolean updatePassword(Long userId, String oldPassword, String newPassword);
}