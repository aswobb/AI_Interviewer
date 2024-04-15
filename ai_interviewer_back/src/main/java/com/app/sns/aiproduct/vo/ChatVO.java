package com.app.sns.aiproduct.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ChatVO {
    @NotBlank(message = "Message cannot be blank")
    private String message;
    private String role;
    private String content;

}
