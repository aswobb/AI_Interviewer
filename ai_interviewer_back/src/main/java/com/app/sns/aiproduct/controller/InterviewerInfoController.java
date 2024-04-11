package com.app.sns.aiproduct.controller;

import com.app.sns.aiproduct.pojo.entity.InterviewerInfo;
import com.app.sns.aiproduct.pojo.vo.InterviewerInfoVO;
import com.app.sns.aiproduct.service.InterviewerInfoService;
import com.app.sns.aiproduct.web.JWTUtil;
import com.app.sns.aiproduct.web.JsonResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/interviewer-info")
public class InterviewerInfoController {

    @Resource
    private InterviewerInfoService interviewerInfoService;

    @PostMapping("/batchCreate")
    public JsonResult batchCreate(@RequestBody InterviewerInfoVO interviewerInfoVO, HttpServletRequest request) {
        Long userId= JWTUtil.getUserIdFromToken(request);
        interviewerInfoService.batchCreate(userId,20);
        return JsonResult.ok();
    }

    @GetMapping("/list")
    public IPage<InterviewerInfo> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       @RequestBody InterviewerInfoVO interviewerInfoVO,
                                       HttpServletRequest request
                                       ) {
        Page<InterviewerInfo> page = new Page<>(pageNum, pageSize);
        QueryWrapper<InterviewerInfo> wrapper = new QueryWrapper<>();
        if (interviewerInfoVO.getInterviewerId() != null) {
            wrapper.eq("interviewer_id", interviewerInfoVO.getInterviewerId());
        }
        if (interviewerInfoVO.getQuestionsNum() != null) {
            wrapper.eq("questions_num", interviewerInfoVO.getQuestionsNum());
        }
        if (interviewerInfoVO.getDeepQuestionsNum() != null) {
            wrapper.eq("deep_questions_num", interviewerInfoVO.getDeepQuestionsNum());
        }
        Long userId= JWTUtil.getUserIdFromToken(request);
        wrapper.eq("userId", userId);
        wrapper.eq("enable", 0);
        return interviewerInfoService.page(page, wrapper);
    }

//    @PostMapping
//    public InterviewerInfo createInterviewerInfo(@RequestBody InterviewerInfoVO interviewerInfoVO) {
//        return interviewerInfoService.createInterviewerInfo(interviewerInfoVO);
//    }
//
//    @PutMapping("/{id}")
//    public InterviewerInfo updateInterviewerInfo(@PathVariable("id") Long id, @RequestBody InterviewerInfoVO interviewerInfoVO) {
//        return interviewerInfoService.updateInterviewerInfo(id, interviewerInfoVO);
//    }
//
//    @GetMapping("/{id}")
//    public InterviewerInfoVO getInterviewerInfo(@PathVariable("id") Long id) {
//        return interviewerInfoService.getInterviewerInfo(id);
//    }
//
//    @DeleteMapping("/{id}")
//    public void deleteInterviewerInfo(@PathVariable("id") Long id) {
//        interviewerInfoService.deleteInterviewerInfo(id);
//    }
}

