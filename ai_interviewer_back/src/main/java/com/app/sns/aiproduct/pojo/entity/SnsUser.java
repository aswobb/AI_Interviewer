package com.app.sns.aiproduct.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@TableName("sns_user")
public class SnsUser {
    private Long id;
    private String username;
    private String userNum;
    private String password;
    private String roleId;
    private String description;
    private Integer enable;
    private Integer usageCount;
    private String contractor;
    private String joinTime;
    private LocalDateTime effectiveTime;
    private Long courseId;
    private Integer balance;
    private Integer remainNum;
    @TableField("gmt_create")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreate;
    @TableField("gmt_update")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtUpdate;
}
