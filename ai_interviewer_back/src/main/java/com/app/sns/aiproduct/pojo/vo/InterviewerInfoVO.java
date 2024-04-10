package com.app.sns.aiproduct.pojo.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class InterviewerInfoVO {
    private Long id;
    private Long userId;
    private String interviewerId;
    private String interviewerName;
    private Integer questionsNum;
    private Integer deepQuestionsNum;
    private Integer enable;
    private LocalDateTime executionDate;
    private LocalDateTime gmtUpdate;
    private LocalDateTime gmtCreate;

    private Integer pageNum = 1;
    private Integer pageSize = 10;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreateStart;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreateEnd;
    private String token;
}
