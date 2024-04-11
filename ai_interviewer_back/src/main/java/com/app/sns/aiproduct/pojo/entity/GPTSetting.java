package com.app.sns.aiproduct.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GPTSetting {
    private Long id;
    private String settingname;
    private String model;
    private String promptcontent;
    private String description;
    private LocalDateTime gmt_update;
    private LocalDateTime gmt_create;
}
