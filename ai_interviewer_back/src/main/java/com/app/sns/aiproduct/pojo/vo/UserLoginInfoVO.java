package com.app.sns.aiproduct.pojo.vo;

import lombok.Data;

/**
 * 用户登录的实体类
 *
 * @author 張
 * @version 0.0.1
 */
@Data
public class UserLoginInfoVO {
    private Long id;
    private String username;
    private String password;
    private Integer enable;
}
