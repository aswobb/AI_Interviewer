//package com.app.sns.aiproduct;
//
//import com.app.sns.aiproduct.ex.ServiceException;
//import com.app.sns.aiproduct.pojo.dto.UserLoginInfoInDTO;
//import com.app.sns.aiproduct.pojo.dto.UserLoginInfoOutDTO;
//import com.app.sns.aiproduct.service.ILoginService;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@Slf4j
//@SpringBootTest
//public class LoginServiceTest {
//    @Autowired
//    private ILoginService loginService;
//
//    @Test
//    public void LoginTest() {
//        UserLoginInfoInDTO userLoginInfoInDTO = new UserLoginInfoInDTO();
//        userLoginInfoInDTO.setUsername("test-001");
//        userLoginInfoInDTO.setPassword("123455");
//        try {
//            UserLoginInfoOutDTO userLoginInfoOutDTO = loginService.loginInfo(userLoginInfoInDTO);
//            log.debug("赋值后的实体类为:{}", userLoginInfoOutDTO);
//        } catch (ServiceException e) {
//            System.out.println(e.getMessage());
//        }
//    }
//}
