package com.app.sns.aiproduct.controller;

import com.app.sns.aiproduct.mapper.CompanyMemberMapper;
import com.app.sns.aiproduct.pojo.dto.UserLoginInfoInDTO;
import com.app.sns.aiproduct.pojo.dto.UserLoginInfoOutDTO;
import com.app.sns.aiproduct.pojo.vo.InterviewerInfoVO;
import com.app.sns.aiproduct.service.ILoginService;
import com.app.sns.aiproduct.vo.MemberVo;
import com.app.sns.aiproduct.web.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * ユーザログイン
 *
 * @author 張
 * @version 0.0.1
 */
@RestController
@Slf4j
@RequestMapping("/users")
public class LoginController {

    @Autowired
    private ILoginService loginService;
    @Autowired
    private CompanyMemberMapper companyMemberMapper;

    @PostMapping("/login")
    public JsonResult loginInfo(@RequestBody UserLoginInfoInDTO userLoginInfoInDTO) {
        log.debug("ログインリクエストの処理を始める:{}", userLoginInfoInDTO);
        UserLoginInfoOutDTO userLoginInfoOutDTO = loginService.loginInfo(userLoginInfoInDTO);
        return JsonResult.ok(userLoginInfoOutDTO);
    }

    @PostMapping("/interviewerLoginInfo")
    public JsonResult interviewerLoginInfo(@RequestBody InterviewerInfoVO interviewerInfoVO) {
        log.debug("ログインリクエストの処理を始める:{}", interviewerInfoVO);
        InterviewerInfoVO interviewInfo = loginService.interviewerLoginInfo(interviewerInfoVO);
        MemberVo memberInfo = companyMemberMapper.getById(interviewInfo.getCompanyMemberId());
        HashMap map = new HashMap<>();
        map.put("InterviewInfo",interviewInfo);
        map.put("memberInfo",memberInfo);
        return JsonResult.ok(map);
    }
}
