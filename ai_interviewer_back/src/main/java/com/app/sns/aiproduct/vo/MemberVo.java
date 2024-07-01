package com.app.sns.aiproduct.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class MemberVo implements Serializable {
    private Long id;
    private String name;
    private Integer uploadStatus;
    private String resume;
}
