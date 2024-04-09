package com.app.sns.aiproduct.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class InterviewerInfo {
    private Long id;
    private Long userId;
    private String interviewerId;
    private Integer interviewerName;
    private Integer questionsNum;
    private Integer deepQuestionsNum;
    private Integer enable;
    @TableField("execution_date")
    private LocalDateTime executionDate;
    @TableField("gmt_update")
    private LocalDateTime gmtUpdate;
    @TableField("gmt_create")
    private LocalDateTime gmtCreate;
}
