package com.app.sns.aiproduct.mapper;


import com.app.sns.aiproduct.pojo.entity.SnsUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<SnsUser> {
}
