package dev.valium.studywithmewebapp.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

// 스프링 시큐리티 설정
@Configuration
@EnableWebSecurity // 설정을 커스텀 할 수 있도록 한다.
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .mvcMatchers("/", "/login", "/sign-up",
                        "/check-email-token", "/email-login", "/check-email-login",
                        "/login-link").permitAll()
                .mvcMatchers(HttpMethod.GET, "/profile/*").permitAll()      // profile은 get만
                .anyRequest().authenticated();      // 그 외 링크는 모두 인증 필요

        // spring security를 통한 로그인, 로그아웃 기능
        http.formLogin()                // 폼 로그인 기능 활성화
                .loginPage("/login")   // 커스텀 로그인 페이지 설정
                .permitAll();

        http.logout()                   // 로그아웃 기능 활성화
                .logoutSuccessUrl("/"); // 로그아웃 시 갈 url
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // PathRequest 하는것 중 Resources/static 에 위치한 흔한것들은 SpringSecurity 를 적용하지 말아라.
        // StaticResourceLocation에 흔한것들이 정의됨
        web.ignoring()
                .mvcMatchers("/node_modules/**") // 추가필터
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
