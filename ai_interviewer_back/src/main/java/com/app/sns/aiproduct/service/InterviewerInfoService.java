package com.app.sns.aiproduct.service;

import com.app.sns.aiproduct.pojo.entity.CsvFile;
import com.app.sns.aiproduct.pojo.entity.InterviewerInfo;
import com.app.sns.aiproduct.pojo.vo.InterviewerInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface InterviewerInfoService extends IService<InterviewerInfo> {
    List<InterviewerInfo> batchCreate(Long userId,Integer accountNum);
    InterviewerInfo createInterviewerInfo(InterviewerInfoVO interviewerInfoVO);
    InterviewerInfo updateInterviewerInfo(Long id, InterviewerInfoVO interviewerInfoVO);
    void deleteInterviewerInfo(Long id);
    InterviewerInfoVO getInterviewerInfo(Long id);

    InterviewerInfo completeInterviewerInfo(Long userId, MultipartFile file);
    CsvFile getCsvFile(Long interviewersInfoId);
    ResponseEntity downLoadCsvs(List<Long> ids) throws IOException;
}
