package com.app.sns.aiproduct.handler;

import com.alibaba.fastjson.JSONObject;
import com.app.sns.aiproduct.constant.ServiceCodeEnum;
import com.app.sns.aiproduct.web.JsonResult;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MyAccessDeniedHandler  implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json");
        String json = JSONObject.toJSONString(JsonResult.fail(ServiceCodeEnum.ERR_NOT_ALLOWED.getCode(), "権限不足"));

        httpServletResponse.getWriter().println(json);
        httpServletResponse.getWriter().flush();
    }
}

