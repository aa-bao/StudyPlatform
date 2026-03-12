package org.example.kaoyanplatform.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 */
@Component
public class JwtUtil {

    // 密钥
    private static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();

    // 默认过期时间：1天 (24小时)
    private static final long DEFAULT_EXPIRATION = 24 * 60 * 60 * 1000L;

    // 记住我过期时间：30天
    private static final long REMEMBER_EXPIRATION = 30 * 24 * 60 * 60 * 1000L;

    // 获取签名密钥
    private SecretKey getSigningKey() {
        return SECRET_KEY;
    }

    /**
     * 生成 JWT token
     * @param userId 用户ID
     * @param username 用户名
     * @param role 用户角色
     * @param remember 是否记住我
     * @return token
     */
    public String generateToken(Long userId, String username, String role, boolean remember) {
        // 设置 claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);

        // 设置过期时间
        long expiration = remember ? REMEMBER_EXPIRATION : DEFAULT_EXPIRATION;

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 从 token 中获取 claims
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从 token 中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        return getClaimsFromToken(token).get("userId", Long.class);
    }

    /**
     * 从 token 中获取用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * 从 token 中获取用户角色
     */
    public String getRoleFromToken(String token) {
        return getClaimsFromToken(token).get("role", String.class);
    }

    /**
     * 检查 token 是否过期
     */
    public boolean isTokenExpired(String token) {
        return getClaimsFromToken(token).getExpiration().before(new Date());
    }

    /**
     * 验证 token
     */
    public boolean validateToken(String token, String username) {
        String tokenUsername = getUsernameFromToken(token);
        return (username.equals(tokenUsername) && !isTokenExpired(token));
    }
}
