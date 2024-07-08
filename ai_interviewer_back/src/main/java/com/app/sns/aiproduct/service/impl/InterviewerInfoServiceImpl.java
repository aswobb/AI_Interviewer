package com.app.sns.aiproduct.service.impl;

import com.app.sns.aiproduct.constant.ServiceCodeEnum;
import com.app.sns.aiproduct.ex.ServiceException;
import com.app.sns.aiproduct.mapper.CsvFileMapper;
import com.app.sns.aiproduct.mapper.InterviewerInfoMapper;
import com.app.sns.aiproduct.mapper.UserMapper;
import com.app.sns.aiproduct.pojo.entity.CsvFile;
import com.app.sns.aiproduct.pojo.entity.InterviewerInfo;
import com.app.sns.aiproduct.pojo.entity.SnsUser;
import com.app.sns.aiproduct.pojo.vo.InterviewerInfoVO;
import com.app.sns.aiproduct.service.InterviewerInfoService;
import com.app.sns.aiproduct.util.EmptyUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class InterviewerInfoServiceImpl extends ServiceImpl<InterviewerInfoMapper, InterviewerInfo>
        implements InterviewerInfoService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private InterviewerInfoMapper interviewerInfoMapper;
    @Autowired
    private CsvFileMapper csvFileMapper;

    @Override
    @Transactional
    public List<InterviewerInfo> batchCreate(Long userId, Integer accountNum) {
        SnsUser snsUser = userMapper.selectById(userId);
        Integer createNum = 0;
        if (accountNum <= 0 || snsUser.getRemainNum() <= 0) {
            throw new ServiceException(ServiceCodeEnum.INSUFFICIENT_BALANCE);
        }
        if (snsUser.getRemainNum() > accountNum) {
            createNum = accountNum;
        } else {
            createNum = snsUser.getRemainNum();
        }
        List<InterviewerInfo> interviewerInfoList = new ArrayList<>();
        for (int i = 0; i < createNum; i++) {
            InterviewerInfo interviewerInfo = new InterviewerInfo();
            interviewerInfo.setInterviewerId(UUID.randomUUID().toString().replaceAll("-", ""));
            interviewerInfo.setEnable(0);
            interviewerInfo.setUserId(userId);
            interviewerInfo.setGmtCreate(LocalDateTime.now());
            interviewerInfoList.add(interviewerInfo);
        }
        Integer oldRemainNum = snsUser.getRemainNum();
        snsUser.setRemainNum(snsUser.getRemainNum() - createNum);
        snsUser.setGmtUpdate(LocalDateTime.now());
        QueryWrapper<SnsUser> snsUserQueryWrapper = new QueryWrapper<>();
        snsUserQueryWrapper.eq("id", snsUser.getId());
        snsUserQueryWrapper.eq("remain_num", oldRemainNum);

        snsUser.setGmtUpdate(LocalDateTime.now());
        userMapper.update(snsUser, snsUserQueryWrapper);
        this.saveBatch(interviewerInfoList);
        return null;
    }

    @Override
    @Transactional
    public InterviewerInfo createInterviewerInfo(InterviewerInfoVO interviewerInfoVO) {
        InterviewerInfo interviewerInfo = new InterviewerInfo();
        interviewerInfo.setGmtCreate(LocalDateTime.now());
        interviewerInfo.setGmtUpdate(LocalDateTime.now());

        save(interviewerInfo);
        return interviewerInfo;
    }

    @Override
    @Transactional
    public InterviewerInfo updateInterviewerInfo(Long id, InterviewerInfoVO interviewerInfoVO) {
        InterviewerInfo interviewerInfo = getById(id);
        if (interviewerInfo == null) {
            throw new ServiceException(ServiceCodeEnum.ERR_NOT_FOUND);
        }
        // interviewerInfo.setXXX(interviewerInfoVO.getXXX());
        interviewerInfo.setInterviewerName(interviewerInfoVO.getInterviewerName());
        interviewerInfo.setGmtUpdate(LocalDateTime.now());
        interviewerInfo.setCompanyMemberId(interviewerInfoVO.getCompanyMemberId());
        interviewerInfo.setUploadStatus(interviewerInfoVO.getUploadStatus());
        updateById(interviewerInfo);
        return interviewerInfo;
    }

    @Override
    public InterviewerInfoVO getInterviewerInfo(Long id) {

        InterviewerInfo interviewerInfo = getById(id);
        if (interviewerInfo == null) {
            throw new ServiceException(ServiceCodeEnum.ERR_NOT_FOUND);
        }

        InterviewerInfoVO interviewerInfoVO = new InterviewerInfoVO();

        return interviewerInfoVO;
    }

    @Override
    @Transactional
    public void deleteInterviewerInfo(Long id) {

        InterviewerInfo interviewerInfo = getById(id);
        if (interviewerInfo == null) {
            throw new ServiceException(ServiceCodeEnum.ERR_NOT_FOUND);
        }

        removeById(id);
    }

    @Override
    @Transactional
    public InterviewerInfo completeInterviewerInfo(Long userId, MultipartFile file) {
        InterviewerInfo interviewerInfo = getById(userId);
        if (interviewerInfo == null) {
            throw new ServiceException(ServiceCodeEnum.ERR_NOT_FOUND);
        }
        interviewerInfo.setEnable(1);
        interviewerInfo.setExecutionDate(LocalDateTime.now());
        interviewerInfo.setGmtUpdate(LocalDateTime.now());
        SnsUser snsUser = userMapper.selectById(interviewerInfo.getUserId());
        snsUser.setUsageCount(EmptyUtil.isNull(snsUser.getUsageCount()) ? 1 : snsUser.getUsageCount() + 1);
        snsUser.setGmtUpdate(LocalDateTime.now());
        userMapper.updateById(snsUser);
        updateById(interviewerInfo);
        try {
            CsvFile csvFile = new CsvFile();
            csvFile.setInterviewersInfoId(userId);
            csvFile.setGmtCreate(LocalDateTime.now());
            csvFile.setFileContent(file.getBytes());
            csvFileMapper.insert(csvFile);
        } catch (Exception e) {
            log.error("save csv file error:{}", e);
            throw new ServiceException(ServiceCodeEnum.ERR_INSERT);

        }

        return interviewerInfo;
    }

    @Override
    public CsvFile getCsvFile(Long interviewersInfoId) {
        QueryWrapper<CsvFile> csvFileQueryWrapper = new QueryWrapper<>();
        csvFileQueryWrapper.lambda().eq(CsvFile::getInterviewersInfoId, interviewersInfoId);
        CsvFile csvFile = csvFileMapper.selectOne(csvFileQueryWrapper);
        return csvFile;
    }

    @Override
    public ResponseEntity downLoadCsvs(List<Long> ids) throws IOException {

        List<CsvFile> csvFiles = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            CsvFile csvFile = getCsvFile(ids.get(i));
            if (EmptyUtil.isNull(csvFile)) {
                throw new ServiceException(ServiceCodeEnum.ERR_NOT_FOUND);
            }
            csvFiles.add(csvFile);
        }
        // 创建一个内存流用于存储 ZIP 文件内容
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(baos);

        // 将每个 CSV 文件添加到 ZIP 中
        for (int i = 0; i < csvFiles.size(); i++) {
            addToZip(zipOut, "test" + i + ".csv", csvFiles.get(i).getFileContent());
        }

        zipOut.close();

        // 构建响应实体并返回 ZIP 文件流
        ByteArrayResource zipFile = new ByteArrayResource(baos.toByteArray());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=files.zip");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .body(zipFile);
    }

    private void addToZip(ZipOutputStream zipOut, String filename, byte[] content) throws IOException {
        ZipEntry zipEntry = new ZipEntry(filename);
        zipOut.putNextEntry(zipEntry);
        zipOut.write(content);
        zipOut.closeEntry();
    }
}
