package com.app.sns.aiproduct.pojo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginInfoOutDTO implements Serializable {
    private Long id;
    private String username;
    private String password;
    private Integer enable;
    private String token;
}
