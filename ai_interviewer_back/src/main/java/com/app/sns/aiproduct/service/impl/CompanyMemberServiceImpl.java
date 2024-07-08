package com.app.sns.aiproduct.service.impl;

import com.app.sns.aiproduct.mapper.CompanyMemberMapper;
import com.app.sns.aiproduct.mapper.InterviewerInfoMapper;
import com.app.sns.aiproduct.pojo.entity.CompanyMember;
import com.app.sns.aiproduct.pojo.entity.InterviewerInfo;
import com.app.sns.aiproduct.service.CompanyMemberService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
    @Autowired
    private InterviewerInfoMapper interviewerInfoMapper;
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

    @Override
    public int updateById(StringBuilder stringBuilder,Long id) {
        CompanyMember companyMember = new CompanyMember();
        companyMember.setId(id);
        companyMember.setResume(String.valueOf(stringBuilder));
        companyMember.setUploadStatus(1);
        int flag1 = companyMemberMapper.updateById(companyMember);

        // 创建更新条件 Wrapper
        UpdateWrapper<InterviewerInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("company_member_id", companyMember.getId()); // 设置更新条件，这里使用用户名作为条件

        // 创建一个 User 对象，设置新的年龄
        InterviewerInfo interviewerInfo = new InterviewerInfo();
        interviewerInfo.setUploadStatus(1);

        // 执行更新操作
        int flag2 = interviewerInfoMapper.update(interviewerInfo, updateWrapper);
        if(flag1==flag2&&flag2==1){
            return flag1;
        }
        else {
            return 0;
        }

    }
}
