package com.cy.newpt.utils;


import com.cy.newpt.pojo.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    /**
     * (核心) 生成一个Token
     * @param account 账户
     * @param role 用户角色
     * @return
     */
    public String generateToken(String account, String role, String name) {
        // 1. 创建一个“claims”声明，这是token的“有效载荷”
        Map<String, Object> claims = new HashMap<>();
        // 2. 可以把任何非敏感信息放进去，比如角色
        claims.put("role", role);
        claims.put("name", name);

        // 3. 使用Jwt.builder() 来构建token
        return Jwts.builder()
                .claims(claims) // 自定义声明
                .subject(account)
                .issuedAt(new Date(System.currentTimeMillis())) // 初始时间
                .expiration(new Date(System.currentTimeMillis() + expirationMs)) // 结束日期
                .signWith(getSigningKey())
                .compact();
        // 实际上，这里返回的是一个Claims，一个接口，如下,所以下面才会有Claims::
        // public interface Claims extends Map<String, Object>
        // 继承自Map，收容你的token，然后里面会包含token和一些方便的方法
    }
    /**
     * (核心) 验证 Token 是否有效
     */
    public boolean validateToken(String token, String account) {
        final String tokenAccount = extractAccount(token);
        return (tokenAccount.equals(account) && !isTokenExpired(token));
    }
    /**
     * (核心) 从 Token 中提取登录账号
     */
    public String extractAccount(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    /**
     * (核心) 从 Token 中提取角色
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> (String) claims.get("role"));
    }
    // ---(以下是内部辅助方法)---
    /**
     * 检查 Token 是否过期
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    /**
     * 提取过期时间
     * // 当你调用：
     * String username = extractClaim(token, Claims::getSubject);
     *
     * // 实际发生的是：
     * // 1. extractAllClaims(token) 执行，返回 Claims 对象
     * // 2. Claims::getSubject 被应用到这个 Claims 对象上
     * // 3. 相当于调用 claims.getSubject()
     *
     * // 可视化流程：
     * token → [extractAllClaims] → Claims对象 → [Claims::getSubject] → 用户名
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    /**
     * 提取所有声明
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser() // 创建解析器构造器
                .verifyWith(getSigningKey())// 设置签名验证密钥
                .build()// 构建 JwtParser 对象
                .parseSignedClaims(token) // 解析并验证 token 签名
                .getPayload(); // 返回Claims部分
    }
    /**
     * 提取单个声明的通用方法
     * Function<Claims, T> claimsResolver：函数接口
     * .apply：函数接口核心方法
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token); // final确保安全
        return claimsResolver.apply(claims);
    }

    /**
     * (最关键) 根据 secretKey 字符串生成一个安全的 SecretKey 对象
     *
     * 安全性：直接使用字符串作为密钥不够安全
     *
     * 格式要求：JWT 签名需要特定格式的密钥对象
     *
     * 标准化：确保密钥长度和格式符合加密算法要求
     */
    private SecretKey getSigningKey() {
        // 1. 将字符串类型的密钥解码成字节格式
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        // 2. 把字节格式的密钥转换成 标准 HMAC-SHA 密钥对象
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
