package com.app.sns.aiproduct.service;

import com.app.sns.aiproduct.pojo.entity.InterviewerInfo;
import com.app.sns.aiproduct.pojo.vo.InterviewerInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InterviewerInfoService extends IService<InterviewerInfo> {
    List<InterviewerInfo> batchCreate(Long userId,Integer accountNum);
    InterviewerInfo createInterviewerInfo(InterviewerInfoVO interviewerInfoVO);
    InterviewerInfo updateInterviewerInfo(Long id, InterviewerInfoVO interviewerInfoVO);
    void deleteInterviewerInfo(Long id);
    InterviewerInfoVO getInterviewerInfo(Long id);

    InterviewerInfo completeInterviewerInfo(Long userId, MultipartFile file);
}
