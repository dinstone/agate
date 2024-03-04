package io.agate.admin.security;

import org.springframework.security.authentication.AuthenticationManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


import io.agate.admin.utils.JwtTokenUtil;
import io.jsonwebtoken.Claims;


public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(JwtTokenUtil.TOKEN_HEADER);

        if (header == null || !header.startsWith(JwtTokenUtil.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken = getUsernamePasswordAuthenticationToken(header);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(String token) {
        token = token.replace(JwtTokenUtil.TOKEN_PREFIX, "");
        Claims claims = JwtTokenUtil.checkJWT(token);
        if (claims != null) {
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            Object role = claims.get("role");
            if (role != null) {
                authorities.add(new SimpleGrantedAuthority(role.toString()));
            }
            return new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
        }

        return null;
    }
}
