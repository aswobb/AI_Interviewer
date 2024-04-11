package com.app.sns.aiproduct.mapper;

import com.app.sns.aiproduct.pojo.vo.UserLoginInfoVO;
import org.springframework.stereotype.Repository;

/**
 * 登录的Mapper接口
 *
 * @author 張
 * @version 0.0.1
 */
@Repository
public interface LoginMapper {
    /**
     * 用户登录信息接口
     *
     * @param username 用户名
     * @return 用户信息
     */
    UserLoginInfoVO getLoginInfo(String username);
}
