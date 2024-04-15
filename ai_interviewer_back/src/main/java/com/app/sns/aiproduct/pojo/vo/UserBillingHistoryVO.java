package com.app.sns.aiproduct.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class UserBillingHistoryVO {
    private Long id;

    private Long userId;

    private Integer addMoney;

    private Integer addMonth;

    private Integer addUsageCount;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime effectiveTime;

    private Long creator;
    private Integer remainNum;
    private LocalDateTime gmtUpdate;

    private LocalDateTime gmtCreate;
}
