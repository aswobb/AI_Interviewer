package com.app.sns.aiproduct.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.app.sns.aiproduct.web.JWTUtil;
import com.app.sns.aiproduct.web.JsonResult;
import com.app.sns.aiproduct.web.ServiceCode;
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
 * JwtAuthorizationFilter 用户请求授权过滤器
 *
 * <p>
 * 提供请求授权功能。用于处理所有 HTTP 请求，并检查是否存在带有正确 token 的 Authorization 标头。
 * 如果 token 有效，则过滤器会将身份验证数据添加到 Spring 的安全上下文中，并授权此次请求访问资源。</p>
 *
 * @author star
 */
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        String path = httpRequest.getRequestURI();
//        if (path.equals("/users/login")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
        // 从 HTTP 请求中获取 token
        String token = request.getHeader("token");
        // 验证 token 是否有效
        if (StringUtils.hasText(token) && JWTUtil.validateToken(token)) {
            // 获取认证信息
            Authentication authentication = JWTUtil.getAuthentication(token);
            // 将认证信息存入 Spring 安全上下文中
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // 放行请求
            filterChain.doFilter(request, response);
        }else{
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out = response.getWriter();
            String json = JSONObject.toJSONString(JsonResult.fail(ServiceCode.ERR_NOT_FOUND, "ユーザー認証に失敗しました"));
            out.write(json);
            out.flush();
            out.close();
        }


    }


}
