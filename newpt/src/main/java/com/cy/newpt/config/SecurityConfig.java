package com.cy.newpt.config;

import com.cy.newpt.filter.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    /**
     * 定义密码编码器的Bean
     * 告诉Spring ：以后加密和匹配密码，就用这个 BCrypt 工具
     * @return
     *
     * 编译时类型：PasswordEncoder（接口）
     * 运行时类型：BCryptPasswordEncoder（具体类）
     * 接口作为类型，体现多态
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    /**
     * 解锁api请求
     * 暂时允许所有的api请求，等做好JWT再把这里锁上
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. 禁用CSRF(后端分享不使用cookie)
                .csrf(csrf -> csrf.disable())
                // 2. 定义授权规则（Authorization）
                .authorizeHttpRequests(authz -> authz
                        // 2.1 登录api，任何人都可以访问
                        .requestMatchers("/api/auth/login").permitAll()
                        // .anyRequest().authenticated() // (我们以后会改成这个)
                        // (可以放行“注册” API) (但是这个系统不支持注册，只能在管理员那里完成注册)
                        // .requestMatchers("/api/auth/register").permitAll()
                        // 2.2 锁门了
                        // 所有/login/admin开头的API...
                        // 必须有"ROLE_ADMIN"角色才能进入
                        // hasRole会自动寻找ROLE_ADMIN
                        .requestMatchers("/api/login/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/student/**").hasRole("STUDENT")
                        .requestMatchers("/test-redis").permitAll()
                        // 2.4 其他所有请求，需要有效验证（验证是否携带token）
                        .anyRequest().authenticated()
                )
                // 3. 设置Session管理为无状态 STATELESS
                //      调用token，就不需要 Spring Boot 的 Session 了
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 告诉Spring Security 在检查用户名密码的过滤器之前，先运行我们自己的保安JwtRequestFilter
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
