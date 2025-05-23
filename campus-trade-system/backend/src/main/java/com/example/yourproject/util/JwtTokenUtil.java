package com.example.yourproject.util;

import com.example.yourproject.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    // Token 有效期，例如7天 (ms)
    public static final long JWT_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000;

    // 从 application.properties 读取密钥，如果不存在则使用默认值
    @Value("${jwt.secret:DefaultSecretKeyForCampusTradeSystemMustBeLongerAndSecure}")
    private String secretString;

    private Key secretKey;

    @PostConstruct
    public void init() {
        // 确保密钥长度至少为256位 (32字节) 以用于 HS256
        if (secretString == null || secretString.length() < 32) {
             System.err.println("Warning: JWT secret key is too short or null. Using a default secure key for HS256.");
             this.secretString = "DefaultSecretKeyForCampusTradeSystemMustBeLongerAndSecureEnoughForHS256AlgorithmUsage";
        }
        this.secretKey = Keys.hmacShaKeyFor(secretString.getBytes());
    }

    // 从token中获取用户名
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // 从token中获取用户ID
    public Integer getUserIdFromToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return claims.get("userId", Integer.class);
    }

    // 从token中获取过期日期
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // 获取token中的所有声明
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 检查token是否已过期
    private Boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            // 解析失败或其他异常视为过期
            return true;
        }
    }

    // 生成token
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        if (user.getEmail() != null) {
            claims.put("email", user.getEmail());
        }
        // 可以添加角色等信息
        // claims.put("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        return doGenerateToken(claims, user.getUsername());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // 用户名作为主题
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 验证token (用户名是否匹配且未过期)
    public Boolean validateToken(String token, User userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            System.err.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }
     
    // 验证token (仅检查是否能解析且未过期，不比较用户)
    public Boolean validateToken(String token) {
        try {
            // 尝试解析token并检查是否过期
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            // 捕获所有可能的JWT异常
            System.err.println("Token validation exception: " + e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
    }
}