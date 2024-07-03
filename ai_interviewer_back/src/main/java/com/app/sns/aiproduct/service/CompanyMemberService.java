package com.app.sns.aiproduct.service;

import com.app.sns.aiproduct.pojo.entity.CompanyMember;

import java.util.List;

public interface CompanyMemberService {
    List<CompanyMember> getCompanyMember();
    int insertMember(CompanyMember companyMember);
    boolean deleteByIds(List<Long> idList);
}
