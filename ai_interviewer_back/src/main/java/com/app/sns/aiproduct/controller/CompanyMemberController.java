package com.app.sns.aiproduct.controller;

import com.app.sns.aiproduct.constant.ServiceCodeEnum;
import com.app.sns.aiproduct.ex.ServiceException;
import com.app.sns.aiproduct.mapper.CompanyMemberMapper;
import com.app.sns.aiproduct.mapper.InterviewerInfoMapper;
import com.app.sns.aiproduct.pojo.entity.CompanyMember;
import com.app.sns.aiproduct.pojo.entity.InterviewerInfo;
import com.app.sns.aiproduct.service.CompanyMemberService;
import com.app.sns.aiproduct.service.UploadFileService;
import com.app.sns.aiproduct.vo.MemberVo;
import com.app.sns.aiproduct.web.JsonResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@RequestMapping("/companyMember")
public class CompanyMemberController {
    @Resource
    private UploadFileService uploadFileService;
    @Autowired
    private CompanyMemberService companyMemberService;
    @Autowired
    private CompanyMemberMapper companyMemberMapper;

    @Autowired
    private InterviewerInfoMapper interviewerInfoMapper;

    @GetMapping("/getAllMebmer")
    public JsonResult getAllList(int userId) {
        List<MemberVo> allList = companyMemberMapper.getAllList(userId);
        return JsonResult.ok(allList);
    }

    @GetMapping("/list")
    public JsonResult list(int userId, int pageNum, int pageSize) {
// 创建分页对象
        Page<CompanyMember> page = new Page<>(pageNum, pageSize);

// 构造查询条件
        QueryWrapper<CompanyMember> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId); // 年龄等于指定值
        Page<CompanyMember> companyMemberPage = companyMemberMapper.selectPage(page, queryWrapper);
// 执行分页查询
        return JsonResult.ok(companyMemberPage);
    }

    @PostMapping("/receiveFile")
    public JsonResult<StringBuilder> receiveFile(@RequestParam("file") MultipartFile file, @RequestParam("id") Long id) throws IOException {
        //        jsonResult.setData("请问有什么可以帮到您的吗？");

        if (file.isEmpty()) {
            throw new ServiceException(ServiceCodeEnum.ERR_FILE_EMPTY);
        }

        StringBuilder stringBuilder = uploadFileService.uploadFile(file);
        int flag = companyMemberService.updateById(stringBuilder,id);

        if (flag == 1) {
            return JsonResult.ok();
        } else {
            throw new ServiceException(ServiceCodeEnum.ERR_READ_FILE);
        }
    }

    @DeleteMapping("/delete")
    public JsonResult deleteMember(Long memberId) {
        int i = companyMemberService.deleteById(memberId);
        return JsonResult.ok(i);
    }

    @Transactional
    @PostMapping("/insert")
    public JsonResult addMember(@RequestBody CompanyMember companyMember) {
        int i = companyMemberService.insertMember(companyMember);

        return JsonResult.ok(i);
    }

    @DeleteMapping("/deleteByIds")
    public JsonResult deleteEntities(@RequestBody List<Long> ids) {
        boolean result = companyMemberService.deleteByIds(ids);
        if (result) {
            return JsonResult.ok();
        }
        throw new ServiceException(ServiceCodeEnum.ERR_READ_FILE);
    }
}
