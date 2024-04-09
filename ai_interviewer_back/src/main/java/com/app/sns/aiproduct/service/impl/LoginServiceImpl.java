package com.app.sns.aiproduct.service.impl;

import com.app.sns.aiproduct.ex.ServiceException;
import com.app.sns.aiproduct.mapper.BillingCourseMapper;
import com.app.sns.aiproduct.mapper.LoginMapper;
import com.app.sns.aiproduct.pojo.dto.UserLoginInfoInDTO;
import com.app.sns.aiproduct.pojo.dto.UserLoginInfoOutDTO;
import com.app.sns.aiproduct.pojo.entity.BillingCourse;
import com.app.sns.aiproduct.pojo.vo.UserLoginInfoVO;
import com.app.sns.aiproduct.service.ILoginService;
import com.app.sns.aiproduct.util.EmptyUtil;
import com.app.sns.aiproduct.web.JWTUtil;
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
                payload.put("username", userLoginInfoOutDTO.getUsername());
                String tokenMessage = JWTUtil.getToken(payload);
                userLoginInfoOutDTO.setToken(tokenMessage);
                return userLoginInfoOutDTO;
            }
            throw new ServiceException(ERR_NOT_FOUND, "密码错误");
        }
        throw new ServiceException(ERR_NOT_FOUND, "用户名不存在");
    }
}
