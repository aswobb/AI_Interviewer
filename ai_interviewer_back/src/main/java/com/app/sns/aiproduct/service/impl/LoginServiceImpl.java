package com.app.sns.aiproduct.service.impl;

import com.app.sns.aiproduct.ex.ServiceException;
import com.app.sns.aiproduct.mapper.BillingCourseMapper;
import com.app.sns.aiproduct.mapper.InterviewerInfoMapper;
import com.app.sns.aiproduct.mapper.LoginMapper;
import com.app.sns.aiproduct.mapper.UserMapper;
import com.app.sns.aiproduct.pojo.dto.UserLoginInfoInDTO;
import com.app.sns.aiproduct.pojo.dto.UserLoginInfoOutDTO;
import com.app.sns.aiproduct.pojo.entity.BillingCourse;
import com.app.sns.aiproduct.pojo.entity.InterviewerInfo;
import com.app.sns.aiproduct.pojo.entity.SnsUser;
import com.app.sns.aiproduct.pojo.vo.InterviewerInfoVO;
import com.app.sns.aiproduct.pojo.vo.UserLoginInfoVO;
import com.app.sns.aiproduct.service.ILoginService;
import com.app.sns.aiproduct.util.EmptyUtil;
import com.app.sns.aiproduct.web.JWTUtil;
import com.app.sns.aiproduct.web.ServiceCode;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.app.sns.aiproduct.web.ServiceCode.ERR_NOT_FOUND;

/**
 * 用户登录接口的实现类
 *
 * @author 張
 * @version 0.0.1
 */
@Slf4j
@Service
public class LoginServiceImpl implements ILoginService {

    @Autowired
    private LoginMapper loginMapper;
    @Autowired
    private BillingCourseMapper billingCourseMapper;
    @Autowired
    private InterviewerInfoMapper interviewerInfoMapper;
    @Autowired
    private UserMapper userMapper;
    @Override
    public UserLoginInfoOutDTO loginInfo(UserLoginInfoInDTO userLoginInfoInDTO) {
        UserLoginInfoOutDTO userLoginInfoOutDTO = new UserLoginInfoOutDTO();
        log.debug("开始处理【用户登录】的业务，参数：{}", userLoginInfoInDTO);
        UserLoginInfoVO loginInfo = loginMapper.getLoginInfo(userLoginInfoInDTO.getUsername());
        if (loginInfo != null) {
            BeanUtils.copyProperties(loginInfo, userLoginInfoOutDTO);
            if (loginInfo.getPassword().equals(userLoginInfoInDTO.getPassword())) {
                //契約会社の管理者の場合
                if("2".equals(userLoginInfoOutDTO.getRoleId())){
                    BillingCourse billingCourse = billingCourseMapper.selectById(userLoginInfoOutDTO.getCourseId());
                    if(!EmptyUtil.isNull(billingCourse)){
                        userLoginInfoOutDTO.setCourseName(billingCourse.getCourseName());
                    }
                }
                Map<String, String> payload = new HashMap<>();
                payload.put("userId", userLoginInfoOutDTO.getId()+"");
                payload.put("userName", userLoginInfoOutDTO.getUsername());
                payload.put("roleId", userLoginInfoOutDTO.getRoleId());
                String tokenMessage = JWTUtil.getToken(payload);
                userLoginInfoOutDTO.setToken(tokenMessage);
                return userLoginInfoOutDTO;
            }
            throw new ServiceException(ERR_NOT_FOUND, "密码错误");
        }
        throw new ServiceException(ERR_NOT_FOUND, "用户名不存在");
    }
    @Override
    public InterviewerInfoVO interviewerLoginInfo(InterviewerInfoVO interviewerInfoVO) {
        InterviewerInfoVO response = new InterviewerInfoVO();
        log.debug("开始处理【用户登录】的业务，参数：{}", interviewerInfoVO);
        QueryWrapper<InterviewerInfo> interviewerInfoQueryWrapper = new QueryWrapper<>();
        interviewerInfoQueryWrapper.lambda().eq(InterviewerInfo::getInterviewerId,interviewerInfoVO.getInterviewerId());
        interviewerInfoQueryWrapper.lambda().eq(InterviewerInfo::getInterviewerName,interviewerInfoVO.getInterviewerName());
        InterviewerInfo interviewerInfo = interviewerInfoMapper.selectOne(interviewerInfoQueryWrapper);
        if (interviewerInfo != null) {
            if(!interviewerInfo.getEnable().equals(0)){
                throw new ServiceException(ServiceCode.ERR_DISABLE, "用户名已失效");
            }
            BeanUtils.copyProperties(interviewerInfo, response);
            Map<String, String> payload = new HashMap<>();
            payload.put("userId", response.getId()+"");
            payload.put("interviewerId", response.getInterviewerId());
            payload.put("interviewerName", response.getInterviewerName());
            payload.put("roleId", "3");
            String tokenMessage = JWTUtil.getToken(payload);
            response.setToken(tokenMessage);
            SnsUser snsUser = userMapper.selectById(response.getUserId());
            response.setContractor(snsUser.getContractor());
            return response;
        }
        throw new ServiceException(ERR_NOT_FOUND, "用户名不存在");
    }
}
