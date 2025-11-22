package com.shop.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * JWT 工具类
 * 提供 JWT 生成和解析功能
 */
@Component
public class JwtUtil {

    @Value("${app.security.jwt.secret}")
    private String jwtSecret;

    @Value("${app.security.jwt.expiration:86400000}")
    private long jwtExpiration;

    /**
     * 生成 JWT token
     *
     * @param username 用户名
     * @param role     用户角色
     * @return JWT token 字符串
     */
    public String generateToken(String username, String role) {
        SecretKey key = getSigningKey();
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key)
                .compact();
    }

    /**
     * 解析 JWT token
     *
     * @param token JWT token 字符串
     * @return Claims 对象
     */
    public Claims parseToken(String token) {
        SecretKey key = getSigningKey();
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取签名密钥
     *
     * @return SecretKey
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 从 token 中提取用户名
     *
     * @param token JWT token
     * @return 用户名
     */
    public String extractUsername(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * 从 token 中提取角色
     *
     * @param token JWT token
     * @return 角色
     */
    public String extractRole(String token) {
        return (String) parseToken(token).get("role");
    }

    /**
     * 验证 token 是否过期
     *
     * @param token JWT token
     * @return true 如果未过期
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
