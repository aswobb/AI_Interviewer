//package com.app.sns.aiproduct.mapper;
//
//import com.app.sns.aiproduct.pojo.vo.UserLoginInfoVO;
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.JWTVerifier;
//import com.auth0.jwt.algorithms.Algorithm;
//import com.auth0.jwt.interfaces.DecodedJWT;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.Calendar;
//import java.util.HashMap;
//import java.util.Map;
//
//@Slf4j
//@SpringBootTest
//public class LoginMapperTest {
//
//    @Autowired
//    private LoginMapper loginMapper;
//
//    @Test
//    void loginTest() {
//        String username = "test-001";
//        UserLoginInfoVO loginInfo = loginMapper.getLoginInfo(username);
//        log.debug("查询到的信息为:{}", loginInfo);
//    }
//
//    @Test
//    void contextLoads() {
//        Map<String, Object> map = new HashMap<>();
//
//        Calendar instance = Calendar.getInstance();
//        instance.add(Calendar.SECOND, 2000);
//
//        String token = JWT.create().withHeader(map) //header
//                .withClaim("userId", 21)//payload
//                .withClaim("username", "zuhao")//payload
//                .withExpiresAt(instance.getTime())//指定令牌的过期时间
//                .sign(Algorithm.HMAC256("!Q@W#E$R")); //签名
//        System.out.println(token);
//    }
//
//    @Test
//    void test() {
//        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MDMwNDgyODksInVzZXJJZCI6MjEsInVzZXJuYW1lIjoienVoYW8ifQ.Mt4qoYAyloiGPjq3MA7f3wqQn5w3X5nEf20HY6T0wzo";
//
//        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256("!Q@W#E$R")).build();
//        DecodedJWT decodedJWT = jwtVerifier.verify(token);
//        System.out.println("用户Id：" + decodedJWT.getClaim("userId").asInt());
//        System.out.println("用户名：" + decodedJWT.getClaim("username").asString());
//        System.out.println("过期时间：" + decodedJWT.getExpiresAt());
//    }
//}
