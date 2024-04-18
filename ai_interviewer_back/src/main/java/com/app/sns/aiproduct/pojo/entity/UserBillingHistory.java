package com.app.sns.aiproduct.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@TableName("user_billing_history")
public class UserBillingHistory {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long courseId;

    private Integer addMoney;

    private Integer addMonth;

    private Integer addUsageCount;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime effectiveTime;
    private Integer remainNum;
    private Long creator;

    private LocalDateTime gmtUpdate;

    private LocalDateTime gmtCreate;
}