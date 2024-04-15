package com.app.sns.aiproduct.web;

import com.app.sns.aiproduct.ex.ServiceException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static com.app.sns.aiproduct.web.ServiceCode.ERR_NOT_FOUND;

@Configuration
public class JWTUtil {
    /**
     * 密钥要自己保管好
     */
    private static String SECRET = "!Q@W#E$R%D^F";

    /**
     * 传入payload信息获取token
     *
     * @param map payload
     * @return token
     */
    public static String getToken(Map<String, String> map) {
        JWTCreator.Builder builder = JWT.create();

        //payload
        map.forEach((k, v) -> {
            builder.withClaim(k, v);
        });

        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MINUTE, 30); //默认30分钟过期

        builder.withExpiresAt(instance.getTime());//指定令牌的过期时间
        return builder.sign(Algorithm.HMAC256(SECRET));
    }

    /**
     * 验证token 合法性
     */
    public static DecodedJWT verify(String token) {
        //如果有任何验证异常，此处都会抛出异常
        return JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token);
    }

    /**
     * 获取token信息方法
     */
    public static Map<String, Claim> getTokenInfo(String token) {

        return JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token).getClaims();
    }

    /**
     * 从请求中获取 token，并提取出用户 ID
     *
     * @param request HTTP 请求对象
     * @return 用户 ID
     * @throws Exception 如果提取用户 ID 失败或 token 不存在，抛出异常
     */
    public static Long getUserIdFromToken(HttpServletRequest request) {
        // 从请求头中获取 token
        String token = request.getHeader("token");
        if (token == null ) {
            throw new ServiceException(ERR_NOT_FOUND,"Token存在しないあるいは間違い");
        }

        // 验证 token 合法性，并获取 token 信息
        Map<String, Claim> claims = JWTUtil.getTokenInfo(token);

        // 从 token 信息中获取用户 ID
        Claim userIdClaim = claims.get("userId");
        if (userIdClaim == null || userIdClaim.isNull()) {
            throw new ServiceException(ERR_NOT_FOUND,"Tokenの中で、userIdが存在しない");
        }

        // 返回用户 ID
        return Long.parseLong(userIdClaim.asString());
    }

    public static boolean validateToken(String token) {
        try {
            verify(token);
            return true;
        } catch (Exception e) {

        }
        return false;
    }
    public static Authentication getAuthentication(String token) {
        DecodedJWT verify = verify(token);
        Map<String, Claim> claims = JWTUtil.getTokenInfo(token);
        // 获取用户名
        String userName = claims.get("userId").asString();
        String roleId = claims.get("roleId").asString();

        // 获取用户角色字符串
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + roleId);
        authorities.add(authority);
        return new UsernamePasswordAuthenticationToken(userName, token, authorities);

    }
}
