package io.agate.admin.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;


@Configuration
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().formLogin().disable().httpBasic().disable()
                .authorizeRequests().antMatchers("/cluster/**", "/gateway/**", "/apps/**", "/route/**").authenticated()
                .anyRequest().permitAll()
                // 设置Session的创建策略为：Spring Security永不创建HttpSession 不使用HttpSession来获取SecurityContext
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().addFilter(new JwtAuthenticationFilter(authenticationManager()));
    }

}
