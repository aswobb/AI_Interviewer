package com.app.sns.aiproduct.pojo.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("csv_files")
public class CsvFile {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long interviewersInfoId;
    private byte[] fileContent;
    private LocalDateTime gmtUpdate;
    private LocalDateTime gmtCreate;
}
