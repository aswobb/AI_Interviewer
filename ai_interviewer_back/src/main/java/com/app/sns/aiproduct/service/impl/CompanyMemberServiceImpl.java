package com.app.sns.aiproduct.service.impl;

import com.app.sns.aiproduct.mapper.CompanyMemberMapper;
import com.app.sns.aiproduct.mapper.InterviewerInfoMapper;
import com.app.sns.aiproduct.pojo.entity.CompanyMember;
import com.app.sns.aiproduct.pojo.entity.InterviewerInfo;
import com.app.sns.aiproduct.service.CompanyMemberService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class CompanyMemberServiceImpl extends ServiceImpl<CompanyMemberMapper,CompanyMember> implements CompanyMemberService {
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

    @Override
    public boolean deleteByIds(List<Long> idList) {
        QueryWrapper<CompanyMember> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", idList); // 使用 in 方法指定多个条件
        return this.remove(queryWrapper);
    }
}
