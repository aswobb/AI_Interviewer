package com.app.sns.aiproduct.pojo.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterviewerInfoVO {
    private Long id;
    private Long userId;
    private String interviewerId;
    private Integer interviewerName;
    private Integer questionsNum;
    private Integer deepQuestionsNum;
    private Integer enable;
    private LocalDateTime executionDate;
    private LocalDateTime gmtUpdate;
    private LocalDateTime gmtCreate;
}
