package com.app.sns.aiproduct.pojo.vo;

import lombok.Data;



@Data
public class ChatGPTSettingVO {
    private Long id;
    private String settingname;
    private String model;
    private String promptcontent;
}
