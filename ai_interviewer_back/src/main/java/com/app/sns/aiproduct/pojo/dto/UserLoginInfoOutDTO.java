package com.app.sns.aiproduct.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserLoginInfoOutDTO implements Serializable {
    private Long id;
    private String username;
    private String password;
    private Integer enable;
    private String token;
    private String roleId;
    private String userNum;
    private String description;
    private Integer usageCount;
    private String contractor;
    private String joinTime;
    private LocalDateTime effectiveTime;
    private Long courseId;
    private String courseName;
    private Integer balance;
    private Integer remainNum;
}
