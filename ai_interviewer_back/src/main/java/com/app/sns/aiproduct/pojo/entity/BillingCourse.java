package com.app.sns.aiproduct.pojo.entity;


import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("billing_course")
public class BillingCourse {

    private Long id;
    private String courseName;
    private Integer courseCost;
    private Integer questionsNum;
    private String description;
    private LocalDateTime gmtUpdate;
    private LocalDateTime gmtCreate;

}