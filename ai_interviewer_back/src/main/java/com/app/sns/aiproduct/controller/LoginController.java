package com.app.sns.aiproduct.controller;

import com.app.sns.aiproduct.pojo.dto.UserLoginInfoInDTO;
import com.app.sns.aiproduct.pojo.dto.UserLoginInfoOutDTO;
import com.app.sns.aiproduct.pojo.vo.InterviewerInfoVO;
import com.app.sns.aiproduct.service.ILoginService;
import com.app.sns.aiproduct.web.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户登录控制器
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

    @PostMapping("/login")
    public JsonResult loginInfo(@RequestBody UserLoginInfoInDTO userLoginInfoInDTO) {
        log.debug("开始处理登录的请求:{}", userLoginInfoInDTO);
        UserLoginInfoOutDTO userLoginInfoOutDTO = loginService.loginInfo(userLoginInfoInDTO);
        return JsonResult.ok(userLoginInfoOutDTO);
    }

    @PostMapping("/interviewerLoginInfo")
    public JsonResult interviewerLoginInfo(@RequestBody InterviewerInfoVO interviewerInfoVO) {
        log.debug("开始处理登录的请求:{}", interviewerInfoVO);
        InterviewerInfoVO response = loginService.interviewerLoginInfo(interviewerInfoVO);
        return JsonResult.ok(response);
    }
}
