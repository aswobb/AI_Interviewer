package com.app.sns.aiproduct.controller;

import com.app.sns.aiproduct.ex.ServiceException;
import com.app.sns.aiproduct.pojo.entity.InterviewerInfo;
import com.app.sns.aiproduct.pojo.vo.InterviewerInfoVO;
import com.app.sns.aiproduct.service.InterviewerInfoService;
import com.app.sns.aiproduct.util.EmptyUtil;
import com.app.sns.aiproduct.web.JWTUtil;
import com.app.sns.aiproduct.web.JsonResult;
import com.app.sns.aiproduct.web.ServiceCode;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.app.sns.aiproduct.web.ServiceCode.ERR_NOT_FOUND;

@RestController
@RequestMapping("/interviewerInfo")
public class InterviewerInfoController {

    @Resource
    private InterviewerInfoService interviewerInfoService;

    @PostMapping("/batchCreate")
    public JsonResult batchCreate(@RequestBody InterviewerInfoVO interviewerInfoVO, HttpServletRequest request) {
        Long userId = JWTUtil.getUserIdFromToken(request);
        interviewerInfoService.batchCreate(userId, 20);
        return JsonResult.ok();
    }

    @GetMapping("/list")
    public IPage<InterviewerInfo> list(@RequestBody InterviewerInfoVO interviewerInfoVO,
                                       HttpServletRequest request
    ) {
        Page<InterviewerInfo> page = new Page<>(interviewerInfoVO.getPageNum(), interviewerInfoVO.getPageSize());
        QueryWrapper<InterviewerInfo> wrapper = new QueryWrapper<>();
        if (interviewerInfoVO.getInterviewerId() != null) {
            wrapper.lambda().eq(InterviewerInfo::getInterviewerId, interviewerInfoVO.getInterviewerId());
        }
        if (interviewerInfoVO.getQuestionsNum() != null) {
            wrapper.lambda().eq(InterviewerInfo::getQuestionsNum, interviewerInfoVO.getQuestionsNum());
        }
        if (interviewerInfoVO.getDeepQuestionsNum() != null) {
            wrapper.lambda().eq(InterviewerInfo::getDeepQuestionsNum, interviewerInfoVO.getDeepQuestionsNum());
        }
        if (interviewerInfoVO.getGmtCreateStart() != null && interviewerInfoVO.getGmtCreateEnd() != null) {
            wrapper.lambda().between(InterviewerInfo::getGmtCreate, interviewerInfoVO.getGmtCreateStart(), interviewerInfoVO.getGmtCreateEnd());
        }
        Long userId = JWTUtil.getUserIdFromToken(request);
        wrapper.lambda().eq(InterviewerInfo::getUserId, userId);
        return interviewerInfoService.page(page, wrapper);
    }

    @PostMapping("/updateInterviewerInfo")
    public JsonResult updateInterviewerInfo(@RequestBody InterviewerInfoVO interviewerInfoVO) {
        if (EmptyUtil.isNull(interviewerInfoVO.getId())) {
            throw new ServiceException(ServiceCode.ERR_PAR_EMPTY, "请求参数为空");
        }
        if (EmptyUtil.isNull(interviewerInfoVO.getInterviewerName())) {
            throw new ServiceException(ServiceCode.ERR_PAR_EMPTY, "请求参数为空");
        }
        return  JsonResult.ok(interviewerInfoService.updateInterviewerInfo(interviewerInfoVO.getId(), interviewerInfoVO));
    }

    @PostMapping("/completeInterviewerInfo")
    public JsonResult completeInterviewerInfo(HttpServletRequest request,@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new ServiceException(ServiceCode.ERR_PAR_EMPTY, "File is empty");
        }
        Long userId = JWTUtil.getUserIdFromToken(request);
        return JsonResult.ok(interviewerInfoService.completeInterviewerInfo(userId, file));
    }
//    @PostMapping
//    public InterviewerInfo createInterviewerInfo(@RequestBody InterviewerInfoVO interviewerInfoVO) {
//        return interviewerInfoService.createInterviewerInfo(interviewerInfoVO);
//    }
//
//    @PutMapping("/{id}")
//    public InterviewerInfo updateInterviewerInfo(@PathVariable("id") Long id, @RequestBody InterviewerInfoVO interviewerInfoVO) {
//
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

