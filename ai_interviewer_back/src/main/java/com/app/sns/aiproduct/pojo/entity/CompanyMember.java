package com.app.sns.aiproduct.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("company_member")
public class CompanyMember {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    private String name;
    private String resume;
    private Integer uploadStatus;
    private String userId;
}
