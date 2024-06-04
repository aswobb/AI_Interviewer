package com.app.sns.aiproduct.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("gpt_key")
public class GptKey {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String chatgptApiKey;
    private String gooleCloudApiKey;
}
