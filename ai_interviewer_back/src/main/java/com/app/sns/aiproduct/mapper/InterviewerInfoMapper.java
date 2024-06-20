package com.app.sns.aiproduct.mapper;

import com.app.sns.aiproduct.pojo.entity.InterviewerInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface InterviewerInfoMapper  extends BaseMapper<InterviewerInfo> {
    int deleteByUserId(@Param("ID")Long id);
}
