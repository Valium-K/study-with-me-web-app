package dev.valium.studywithmewebapp.config;

import dev.valium.studywithmewebapp.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

// 스프링 시큐리티 설정
@Configuration
@EnableWebSecurity // 설정을 커스텀 할 수 있도록 한다.
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AccountService accountService;
    private final DataSource dataSource;    // JPA를 사용중이니 Bean으로 등록이 이미 돼어있다.

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
        http.formLogin()                            // 폼 로그인 기능 활성화
                .usernameParameter("username")      // 빼도 된다. 기본값과 같기에 but 이런 설정이있다.
                .passwordParameter("password")     // 빼도 된다. 기본값과 같기에 but 이런 설정이있다.
                .loginPage("/login")                // 커스텀 로그인 페이지 설정
                .permitAll();

        // 제공하는 rememberMe 중 가장 안전한 방식 설정
        http.rememberMe()
                .rememberMeParameter("remember-me")      // 빼도 된다. 기본값과 같기에 but 이런 설정이있다.
                .userDetailsService(accountService)
                .tokenRepository(tokenRepository());     // fx(username, 토큰(랜덤, 매번바뀜), 시리즈(랜덤, 고정)) 의 정보를 저장할 저장소

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

    // remember-me 설정 bean
    @Bean
    public PersistentTokenRepository tokenRepository() {
        // JdbcTokenRepositoryImpl의 CREATE_TABLE_SQL에 해당하는 db 스키마를 매핑해줘야한다.
        // 개발시에는 jpa가 자동으로 만들어주게 할 예정이므로 해당 스키마에 맞는 엔티티를 만들어줘야한다.
        // TODO JdbcTokenRepositoryImpl.CREATE_TABLE_SQL의 SQL문에 맞는 Entity 생성

        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);

        return jdbcTokenRepository;
    }
}
