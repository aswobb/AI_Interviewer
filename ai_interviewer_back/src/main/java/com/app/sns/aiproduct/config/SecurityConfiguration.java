package com.app.sns.aiproduct.config;

import com.app.sns.aiproduct.handler.MyAccessDeniedHandler;
import com.app.sns.aiproduct.interceptor.JwtAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Web 安全配置
 *
 * @author star
 **/
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

//    @Autowired
//    private CorsFilter corsFilter;


    /**
     * 使用 Spring Security 推荐的加密方式进行登录密码的加密
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * 此方法配置的资源路径不会进入 Spring Security 机制进行验证
     */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers(HttpMethod.OPTIONS, "/**")
                .antMatchers("/app/**/*.{js,html}")
                .antMatchers("/v2/api-docs/**")
                .antMatchers("/i18n/**")
                .antMatchers("/test/**")
                .antMatchers("/h2")
                .antMatchers("/content/**")
                .antMatchers("/webjars/springfox-swagger-ui/**")
                .antMatchers("/swagger-resources/**")
                .antMatchers("/swagger-ui.html")
                .antMatchers("/users/interviewerLoginInfo")
                .antMatchers("/users/login");
    }

    /**
     * 定义安全策略，设置 HTTP 访问规则
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
//                // 当用户无权访问资源时发送 401 响应
//                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                // 当用户访问资源因权限不足时发送 403 响应
//                .authenticationEntryPoint(new MyEntryPoint())
                .accessDeniedHandler(new MyAccessDeniedHandler())

                .and()
                // 禁用 CSRF
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .logout().logoutUrl("/auth/logout").and()
                .authorizeRequests()
                // 指定路径下的资源需要进行验证后才能访问
                .antMatchers("/").permitAll()
                // 配置登录地址
                .antMatchers(HttpMethod.POST, "/snsUser/updatePassword").hasAnyRole("2")
                .antMatchers(HttpMethod.POST, "/snsUser/list").hasAnyRole("0","1")
                .antMatchers(HttpMethod.POST, "/snsUser/create").hasAnyRole("0","1")
                .antMatchers(HttpMethod.POST, "/snsUser/update").hasAnyRole("0","1")
                .antMatchers(HttpMethod.DELETE, "/snsUser/delete/{id}").hasAnyRole("0", "1")
                .antMatchers(HttpMethod.POST, "/interviewerInfo/batchCreate").hasAnyRole("2")
                .antMatchers(HttpMethod.POST, "/interviewerInfo/list").hasAnyRole("2")
                .antMatchers(HttpMethod.POST, "/interviewerInfo/updateInterviewerInfo").hasAnyRole("2")
                .antMatchers(HttpMethod.POST, "/interviewerInfo/completeInterviewerInfo").hasAnyRole("3")
                .antMatchers(HttpMethod.GET, "/interviewerInfo/downLoadCsv/{fileId}").hasAnyRole("2")
                .antMatchers(HttpMethod.POST, "/chat/sendMessage").hasAnyRole("3")
                .antMatchers(HttpMethod.POST, "/chat/sendMessageByGoogleCloud").hasAnyRole("3")
                .antMatchers(HttpMethod.POST, "/chat/sendContentByGoogleCloud").hasAnyRole("3")
                .antMatchers(HttpMethod.POST, "/upload").hasAnyRole("3")
                // 其他请求需验证
                .anyRequest().authenticated()
                .and()
                // 不需要 session（不创建会话）
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .apply(securityConfigurationAdapter());
//        super.configure(http);
    }

    private JwtConfigurer securityConfigurationAdapter() throws Exception{
        return new JwtConfigurer(new JwtAuthorizationFilter(authenticationManager()));
    }
}
