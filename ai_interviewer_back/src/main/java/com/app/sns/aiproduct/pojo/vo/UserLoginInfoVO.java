package com.app.sns.aiproduct.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

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
    private String roleId;
    private String userNum;
    private String description;
    private Integer usageCount;
    private String contractor;
    private String joinTime;
    private LocalDateTime effectiveTime;
    private Long courseId;
    private Integer balance;
    private Integer remainNum;
}
