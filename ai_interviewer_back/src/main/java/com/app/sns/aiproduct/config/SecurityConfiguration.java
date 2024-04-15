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
 * spring security 配置クラス
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
     * Spring Securityが推奨する暗号化方式を使用して、ログインパスワードを暗号化します。
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * このメソッドで設定されたリソースパスは、Spring Securityの検証メカニズムを通過しません。
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
     * セキュリティポリシーを定義し、HTTPアクセスルールを設定します。
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .accessDeniedHandler(new MyAccessDeniedHandler())

                .and()
                // 禁用 CSRF
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .logout().logoutUrl("/auth/logout").and()
                .authorizeRequests()
                // 指定されたパス下のリソースへのアクセスには検証が必要です
                .antMatchers("/").permitAll()
                // ログインアドレスの設定
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
                // 他のリクエストには検証が必要
                .anyRequest().authenticated()
                .and()
                // セッションは必要ない（セッションを作成しない）
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
