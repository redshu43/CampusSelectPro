package com.cy.newpt.filter;

import com.cy.newpt.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT请求过滤器
 * 拦截每一个请求，检查Header中的合法token
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    /**
     *
     * @param request 封装了HTTP请求的所有信息
     * @param response 用于构建HTTP响应
     * @param filterChain 过滤器链，用于继续执行后续的过滤器或目标Servlet
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 从请求头 (Header) 中获取 "Authorization"
        final String authHeader = request.getHeader("Authorization");

        // 【监控 1】看看有没有收到 Header
        System.out.println("--- 1. 收到请求: " + request.getRequestURI());
        System.out.println("--- 2. Authorization 头: " + authHeader);

        String account = null;
        String jwt = null;
        String role = null;

        // 2. 检查header是否存在，并且是否以"Bearer "开头
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // "Bearer "后面就是token
            try {
                // 3. 解析账号和角色
                account = jwtUtil.extractAccount(jwt);
                role = jwtUtil.extractRole(jwt);

                // 【监控 2】看看解析结果
                System.out.println("--- 3. 解析成功 -> 账号: " + account + ", 角色: " + role);

            } catch (ExpiredJwtException e) {
                // token过期
                System.err.println("Token has expired");
            } catch (Exception e) {
                // token解析失败
                System.err.println("Invalid token");

                System.out.println("--- [错误] Token 解析失败: " + e.getMessage());
            }
        }

        // 4. 验证 token 并设置  安全上下文
        // 如果account 不是 null
        //    并且 SecurityContextHolder.getContext().getAuthentication() 是 null (即“当前还没有人登录”)
        if(account != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 【监控 3】正在放行
            System.out.println("--- 4. 正在设置安全上下文 (放行)...");

            // 5. 相信token里面的信息
            // 根据 token 里面的 role， 创建Spring Security 的权限对象
            GrantedAuthority authority = new SimpleGrantedAuthority(role);

            // 6. 手动创建一个认证凭证
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    account, // 当事人
                    null, // 密码，因为使用token所以不再需要密码
                    Collections.singletonList(authority) // 权限
            );

            // 7. 把认证凭证放入上下文
            // Spring Security 看到就知道ok，这个人登录了，角色是role
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        // 8. 放行请求，让他继续访问 Controller
        filterChain.doFilter(request, response);
    }
}
