package com.app.sns.aiproduct.service;

import com.app.sns.aiproduct.pojo.dto.UserLoginInfoInDTO;
import com.app.sns.aiproduct.pojo.dto.UserLoginInfoOutDTO;
import com.app.sns.aiproduct.pojo.vo.InterviewerInfoVO;

/**
 * 登录业务接口
 *
 * @author 張
 * @version 0.0.1
 */
public interface ILoginService {
    /**
     * 用户登陆的业务接口
     *
     * @param userLoginInfoInDTO 用户输入的信息
     */
    UserLoginInfoOutDTO loginInfo(UserLoginInfoInDTO userLoginInfoInDTO);

    InterviewerInfoVO interviewerLoginInfo(InterviewerInfoVO interviewerInfoVO);
}
