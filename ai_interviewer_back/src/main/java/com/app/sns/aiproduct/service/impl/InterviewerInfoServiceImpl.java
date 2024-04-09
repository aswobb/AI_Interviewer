package com.app.sns.aiproduct.service.impl;

import com.app.sns.aiproduct.ex.ServiceException;
import com.app.sns.aiproduct.mapper.InterviewerInfoMapper;
import com.app.sns.aiproduct.mapper.UserMapper;
import com.app.sns.aiproduct.pojo.entity.InterviewerInfo;
import com.app.sns.aiproduct.pojo.entity.SnsUser;
import com.app.sns.aiproduct.pojo.vo.InterviewerInfoVO;
import com.app.sns.aiproduct.service.InterviewerInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.app.sns.aiproduct.web.ServiceCode.INSUFFICIENT_BALANCE;
import static com.app.sns.aiproduct.web.ServiceCode.ERR_NOT_FOUND;
public class InterviewerInfoServiceImpl extends ServiceImpl<InterviewerInfoMapper, InterviewerInfo>
        implements InterviewerInfoService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private InterviewerInfoMapper interviewerInfoMapper;

    public List<InterviewerInfo> batchCreate(Long userId, Integer accountNum) {
        SnsUser snsUser = userMapper.selectById(userId);
        Integer createNum = 0;
        if (accountNum <= 0 || snsUser.getRemainNum() <= 0) {
            throw new ServiceException(INSUFFICIENT_BALANCE, "余额不足");
        }
        if (snsUser.getRemainNum() > accountNum) {
            createNum = accountNum;
        } else {
            createNum = snsUser.getRemainNum();
        }
        List<InterviewerInfo> interviewerInfoList = new ArrayList<>();
        for (int i = 0; i < createNum; i++) {
            InterviewerInfo interviewerInfo = new InterviewerInfo();
            interviewerInfo.setInterviewerId(UUID.randomUUID().toString());
            interviewerInfo.setEnable(0);
            interviewerInfo.setUserId(userId);
            interviewerInfo.setGmtUpdate(LocalDateTime.now());
            interviewerInfo.setGmtCreate(LocalDateTime.now());
            interviewerInfoList.add(interviewerInfo);
        }
        this.saveBatch(interviewerInfoList);
        return null;
    }

    @Override
    @Transactional
    public InterviewerInfo createInterviewerInfo(InterviewerInfoVO interviewerInfoVO) {
        InterviewerInfo interviewerInfo = new InterviewerInfo();
        // 设置属性
        // interviewerInfo.setXXX(interviewerInfoVO.getXXX());
        // 设置其他属性
        interviewerInfo.setGmtCreate(LocalDateTime.now());
        interviewerInfo.setGmtUpdate(LocalDateTime.now());
        // 插入数据
        save(interviewerInfo);
        return interviewerInfo;
    }

    @Override
    @Transactional
    public InterviewerInfo updateInterviewerInfo(Long id, InterviewerInfoVO interviewerInfoVO) {
        // 判断id是否存在
        InterviewerInfo interviewerInfo = getById(id);
        if (interviewerInfo == null) {
            throw new ServiceException(ERR_NOT_FOUND,"InterviewerInfo with id " + id + " not found.");
        }
        // 更新属性
        // interviewerInfo.setXXX(interviewerInfoVO.getXXX());
        // 设置其他属性
        interviewerInfo.setGmtUpdate(LocalDateTime.now());
        // 更新数据
        updateById(interviewerInfo);
        return interviewerInfo;
    }

    @Override
    public InterviewerInfoVO getInterviewerInfo(Long id) {
        // 查询数据
        InterviewerInfo interviewerInfo = getById(id);
        if (interviewerInfo == null) {
            throw new ServiceException(ERR_NOT_FOUND,"InterviewerInfo with id " + id + " not found.");
        }
        // 转换为 VO 对象
        InterviewerInfoVO interviewerInfoVO = new InterviewerInfoVO();
        // 设置属性
        // interviewerInfoVO.setXXX(interviewerInfo.getXXX());
        // 设置其他属性
        return interviewerInfoVO;
    }

    @Override
    @Transactional
    public void deleteInterviewerInfo(Long id) {
        // 判断id是否存在
        InterviewerInfo interviewerInfo = getById(id);
        if (interviewerInfo == null) {
            throw new ServiceException(ERR_NOT_FOUND,"InterviewerInfo with id " + id + " not found.");
        }
        // 删除数据
        removeById(id);
    }
}
