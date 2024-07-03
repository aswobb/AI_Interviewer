package com.app.sns.aiproduct.mapper;

import com.app.sns.aiproduct.pojo.entity.CompanyMember;
import com.app.sns.aiproduct.vo.MemberVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CompanyMemberMapper extends BaseMapper<CompanyMember> {
    List<MemberVo> getAllList(@Param("USERID")int userId);
    MemberVo getById(@Param("ID")int id);
    Long getLastID();
    int deleteByUserId(@Param("USERID")Long userId);
}
