package com.app.sns.aiproduct.interceptor;

import com.app.sns.aiproduct.web.JWTUtil;
import com.app.sns.aiproduct.web.JsonResult;
import com.app.sns.aiproduct.web.ServiceCode;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HashMap<String, String> map = new HashMap<>();
        //从http请求头获取token
        String token = request.getHeader("token");
        try {
            //如果验证成功放行请求
            DecodedJWT verify = JWTUtil.verify(token);
            return true;
        } catch (Exception exception) {
            map.put("msg", "認証に失敗しました：" + exception);
        }
        response.setContentType("application/json:charset=UTF=8");
        response.setCharacterEncoding("utf-8");
        JsonResult<Void> jsonResult = JsonResult.fail(ServiceCode.ERR_NOT_FOUND,
                "ユーザー認証に失敗しました");
        String s = new ObjectMapper().writeValueAsString(jsonResult);
        response.getWriter().println(s);
        return false;
    }

}
