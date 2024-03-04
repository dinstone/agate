package io.agate.admin.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTokenUtil {
    // Token请求头
    public static final String TOKEN_HEADER = "Authorization";
    // Token前缀
    public static final String TOKEN_PREFIX = "Bearer ";

    // 签名主题
    public static final String SUBJECT = "piconjo";
    // 过期时间
    public static final long EXPIRITION = 1000 * 24 * 60 * 60 * 7;
    // 应用密钥
    public static final String APPSECRET_KEY = "HfkjksFKLJISJFKLFKWJFQFIQWIOFJQOFFQGGSDGFFJIQOEUFIEJFIOQWEFHFQOK5FKOIQWUFFEFE423FIQEOFJHUEWHFKASKDLQWJIFSJDJKFHJIJWO";
    // 角色权限声明
    private static final String ROLE_CLAIMS = "role";

    /**
     * 生成Token
     */
    public static String createToken(String username, String role) {
        String token = Jwts
                .builder()
                .claims().subject(username).add(ROLE_CLAIMS, role)
                .issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + EXPIRITION))
                .and().signWith(SignatureAlgorithm.HS256, APPSECRET_KEY).compact();
        return token;
    }

    /**
     * 校验Token
     */
    public static Claims checkJWT(String token) {
        try {
            final Claims claims = Jwts.parser().setSigningKey(APPSECRET_KEY).build().parseClaimsJws(token).getBody();
            return claims;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从Token中获取username
     */
    public static String getUsername(String token) {
        Claims claims = Jwts.parser().setSigningKey(APPSECRET_KEY).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    /**
     * 从Token中获取用户角色
     */
    public static String getUserRole(String token) {
        Claims claims = Jwts.parser().setSigningKey(APPSECRET_KEY).build().parseClaimsJws(token).getBody();
        return claims.get(ROLE_CLAIMS).toString();
    }

    /**
     * 校验Token是否过期
     */
    public static boolean isExpiration(String token) {
        Claims claims = Jwts.parser().setSigningKey(APPSECRET_KEY).build().parseClaimsJws(token).getBody();
        return claims.getExpiration().before(new Date());
    }

    public static void main(String[] args) {
        String t = JwtTokenUtil.createToken("agate", "admin");
        System.out.println(t);
        System.out.println(JwtTokenUtil.getUsername(t) + " : " + JwtTokenUtil.getUserRole(t));
    }
}