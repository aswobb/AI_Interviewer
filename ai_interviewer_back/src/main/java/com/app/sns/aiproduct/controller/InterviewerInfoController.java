package com.app.sns.aiproduct.controller;

import com.app.sns.aiproduct.constant.ServiceCodeEnum;
import com.app.sns.aiproduct.ex.ServiceException;
import com.app.sns.aiproduct.pojo.entity.CsvFile;
import com.app.sns.aiproduct.pojo.entity.InterviewerInfo;
import com.app.sns.aiproduct.pojo.vo.InterviewerInfoVO;
import com.app.sns.aiproduct.service.InterviewerInfoService;
import com.app.sns.aiproduct.util.EmptyUtil;
import com.app.sns.aiproduct.web.JWTUtil;
import com.app.sns.aiproduct.web.JsonResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/interviewerInfo")
public class InterviewerInfoController {

    @Resource
    private InterviewerInfoService interviewerInfoService;

    /**
     * 契約会社は面接者情報を大量作成する
     * @param interviewerInfoVO
     * @param request
     * @return
     */
    @PostMapping("/batchCreate")
    public JsonResult batchCreate(@RequestBody InterviewerInfoVO interviewerInfoVO, HttpServletRequest request) {
        Long userId = JWTUtil.getUserIdFromToken(request);
        interviewerInfoService.batchCreate(userId, 20);
        return JsonResult.ok();
    }

    /**
     * 面接者情報をリストする
     * @param interviewerInfoVO
     * @param request
     * @return
     */
    @GetMapping("/list")
    public JsonResult list(@ModelAttribute   InterviewerInfoVO interviewerInfoVO,
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
        return JsonResult.ok(interviewerInfoService.page(page, wrapper));
    }

    /**
     * 面接者情報を更新する
     * @param interviewerInfoVO
     * @return
     */
    @PostMapping("/updateInterviewerInfo")
    public JsonResult updateInterviewerInfo(@RequestBody InterviewerInfoVO interviewerInfoVO) {
        if (EmptyUtil.isNull(interviewerInfoVO.getId())) {
            throw new ServiceException(ServiceCodeEnum.ERR_PAR_EMPTY);
        }
        if (EmptyUtil.isNull(interviewerInfoVO.getInterviewerName())) {
            throw new ServiceException(ServiceCodeEnum.ERR_PAR_EMPTY);
        }
        return  JsonResult.ok(interviewerInfoService.updateInterviewerInfo(interviewerInfoVO.getId(), interviewerInfoVO));
    }

    /**
     * 面接の流れ完了して、CSVファイルをアップロード
     * @param request
     * @param file
     * @return
     */
    @PostMapping("/completeInterviewerInfo")
    public JsonResult completeInterviewerInfo(HttpServletRequest request,@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new ServiceException(ServiceCodeEnum.ERR_PAR_EMPTY);
        }
        Long userId = JWTUtil.getUserIdFromToken(request);
        return JsonResult.ok(interviewerInfoService.completeInterviewerInfo(userId, file));
    }


    /**
     * CSVファイル　ダウンロード
     * @param fileId
     * @return
     */
    @GetMapping("/downLoadCsv/{fileId}")
    public ResponseEntity  downLoadCsv(@PathVariable Long fileId) {
        if (EmptyUtil.isNull(fileId)) {
            throw new ServiceException(ServiceCodeEnum.ERR_PAR_EMPTY);
        }
        CsvFile csvFile = interviewerInfoService.getCsvFile(fileId);
        if (EmptyUtil.isNull(csvFile)) {
            throw new ServiceException(ServiceCodeEnum.ERR_NOT_FOUND);
        }
        ByteArrayResource resource = new ByteArrayResource(csvFile.getFileContent());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=output.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(csvFile.getFileContent().length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
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

