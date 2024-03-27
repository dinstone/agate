/*
 * Copyright (C) 2020~2024 dinstone<dinstone@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.agate.admin.utils;

import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUtil {
    // Token请求头
    public static final String TOKEN_HEADER = "Authorization";
    // Token前缀
    public static final String TOKEN_PREFIX = "Bearer ";

    // 过期时间
    public static final long EXPIRATION = 24 * 60 * 60 * 1000;
    // 应用密钥
    public static final String SECRET_KEY = "HfkjksFKLJISJFKLFKWJFQFIQWIOFJQOFFQGGSDGFFJIQOEUFIEJFIOQWEFHFQOK5FKOIQWUFFEFE423FIQEOFJHUEWHFKASKDLQWJIFSJDJKFHJIJWO";

    // 角色权限声明
    private static final String ROLE_CLAIMS = "role";

    /**
     * 生成Token
     */
    public static String createToken(String username, String role) {
        String token = Jwts
                .builder()
                .claims().subject(username).add(ROLE_CLAIMS, role)
                .issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .and().signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
        return token;
    }

    /**
     * 校验Token
     */
    public static Claims checkJWT(String token) {
        try {
            final Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
            return claims;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从Token中获取username
     */
    public static String getUsername(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public static String getUserRole(Claims claims) {
        Object r = claims.get(ROLE_CLAIMS);
        if (r != null) {
            return r.toString();
        }
        return null;
    }

    /**
     * 从Token中获取用户角色
     */
    public static String getUserRole(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
        return getUserRole(claims);
    }

    /**
     * 校验Token是否过期
     */
    public static boolean isExpiration(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
        return claims.getExpiration().before(new Date());
    }

    public static void main(String[] args) {
        String t = JwtUtil.createToken("agate", "admin");
        System.out.println(t);
        System.out.println(JwtUtil.getUsername(t) + " : " + JwtUtil.getUserRole(t));
    }
}