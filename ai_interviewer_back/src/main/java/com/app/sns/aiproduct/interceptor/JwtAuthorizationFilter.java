package com.app.sns.aiproduct.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.app.sns.aiproduct.constant.ServiceCodeEnum;
import com.app.sns.aiproduct.web.JWTUtil;
import com.app.sns.aiproduct.web.JsonResult;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author star
 */
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("token");
        if (StringUtils.hasText(token) && JWTUtil.validateToken(token)) {
            Authentication authentication = JWTUtil.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        }else{
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out = response.getWriter();
            String json = JSONObject.toJSONString(JsonResult.fail(ServiceCodeEnum.ERR_NOT_FOUND.getCode(), "ユーザー認証に失敗しました"));
            out.write(json);
            out.flush();
            out.close();
        }


    }


}
