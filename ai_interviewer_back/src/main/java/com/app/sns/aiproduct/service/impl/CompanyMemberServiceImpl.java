package com.app.sns.aiproduct.service.impl;

import com.app.sns.aiproduct.mapper.CompanyMemberMapper;
import com.app.sns.aiproduct.pojo.entity.CompanyMember;
import com.app.sns.aiproduct.service.CompanyMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class CompanyMemberServiceImpl implements CompanyMemberService {
    private Lock lock = new ReentrantLock();
    @Autowired
    private CompanyMemberMapper companyMemberMapper;
    @Override
    public List<CompanyMember> getCompanyMember() {
        List<CompanyMember> companyMembers = companyMemberMapper.selectList(null);
        return companyMembers;
    }

    @Override
    public int insertMember(CompanyMember companyMember) {
        int insert = companyMemberMapper.insert(companyMember);

        return insert;
    }
}
