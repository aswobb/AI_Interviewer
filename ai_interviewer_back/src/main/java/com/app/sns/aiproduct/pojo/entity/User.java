package com.app.sns.aiproduct.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 *
 * @author 張
 * @version 0.0.1
 */
@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String description;
    private Integer enable;
    private Integer usage_count;
    private LocalDateTime gmt_create;
}
