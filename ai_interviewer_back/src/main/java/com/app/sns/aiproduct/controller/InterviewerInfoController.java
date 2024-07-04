package com.app.sns.aiproduct.controller;

import com.app.sns.aiproduct.constant.ServiceCodeEnum;
import com.app.sns.aiproduct.ex.ServiceException;
import com.app.sns.aiproduct.lock.LockManager;
import com.app.sns.aiproduct.pojo.entity.CsvFile;
import com.app.sns.aiproduct.pojo.entity.InterviewerInfo;
import com.app.sns.aiproduct.pojo.entity.SnsUser;
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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/interviewerInfo")
@Api
public class InterviewerInfoController {

    @Resource
    private InterviewerInfoService interviewerInfoService;
    @Resource
    private LockManager lockManager;

    /**
     * 契約会社は面接者情報を大量作成する
     *
     * @param interviewerInfoVO
     * @param request
     * @return
     */
    @PostMapping("/batchCreate")
    @ApiOperation(value = "hello world 接口")
    public JsonResult batchCreate(@RequestBody InterviewerInfoVO interviewerInfoVO, HttpServletRequest request) {
        Long userId = JWTUtil.getUserIdFromToken(request);

        ReentrantLock lock = lockManager.getLock(SnsUser.class, userId);

        lock.lock();
        try {
            interviewerInfoService.batchCreate(userId, 20);
            return JsonResult.ok();
        } catch (Exception e) {
            throw e;
        } finally {
            lock.unlock();
        }

    }

    /**
     * 面接者情報をリストする
     *
     * @param interviewerInfoVO
     * @param request
     * @return
     */
    @GetMapping("/list")
    public JsonResult list(@ModelAttribute InterviewerInfoVO interviewerInfoVO,
                           HttpServletRequest request
    ) {
        int pageSize = interviewerInfoVO.getPageSize();
        Page<InterviewerInfo> page;
        if (pageSize == -1) {
            page = new Page<>(0, Integer.MAX_VALUE);
        } else {
            page = new Page<>(interviewerInfoVO.getPageNum(), interviewerInfoVO.getPageSize());
        }
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
     *
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
        return JsonResult.ok(interviewerInfoService.updateInterviewerInfo(interviewerInfoVO.getId(), interviewerInfoVO));
    }

    /**
     * 面接の流れ完了して、CSVファイルをアップロード
     *
     * @param request
     * @param file
     * @return
     */
    @PostMapping("/completeInterviewerInfo")
    public JsonResult completeInterviewerInfo(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new ServiceException(ServiceCodeEnum.ERR_PAR_EMPTY);
        }
        Long userId = JWTUtil.getUserIdFromToken(request);
        ReentrantLock lock = lockManager.getLock(SnsUser.class, userId);

        lock.lock();
        try {
            return JsonResult.ok(interviewerInfoService.completeInterviewerInfo(userId, file));
        } catch (Exception e) {
            throw e;
        } finally {
            lock.unlock();
        }


    }


    /**
     * CSVファイル　ダウンロード
     *
     * @param fileId
     * @return
     */
    @GetMapping("/downLoadCsv/{fileId}")
    public ResponseEntity downLoadCsv(@PathVariable Long fileId) {
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

    /**
     * CSVファイル　複数ダウンロード
     *
     * @param ids
     * @return
     */
    @PostMapping("/downLoadCsvs")
    public ResponseEntity downLoadCsvs(@RequestBody List<Long> ids) throws IOException {
        return interviewerInfoService.downLoadCsvs(ids);
    }

}