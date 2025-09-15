package com.altimedia.example.oauth.config;

import com.altimedia.example.oauth.domain.Role;
import com.altimedia.example.oauth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;

    /*
    .csrfConfig.disable() CSRF 보호 기능 비활성화
    .headers().frameOptions().disable() HTTP Header의 X-Frame-Options를 비활성화해서 <iframe> 태그로 페이지를 embed(삽입)할 수 있게 함
    .authorizeHttpRequests().requestMatchers("/", "/Savory-gh-pages/").permitAll(): 지정한 경로에 접근할 때 인증을 요구하지 않음
    .formLogin().loginPage("/login").defaultSuccessUrl("/"): Form 기반 로그인 방식 활성화, 로그인 페이지 경로 지정, 로그인 성공 시 페이지 경로 지정
    .logout().logoutSuccessUrl("/"): 로그아웃 성공 시 페이지 경로 지정
    .oauth2Login().userInfoEndpoint().userService(customOAuth2UserService): OAuth2.0 사용 시 사용자 정보 처리를 위한 서비스 빈(커스텀) 사용
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrfConfig -> csrfConfig.disable())
                .headers(headersConfig -> headersConfig.frameOptions(frameOptionsConfig ->
                        frameOptionsConfig.disable()))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/", "/manual/**").permitAll())
//                .formLogin(formLoginConfig -> formLoginConfig.loginPage("/login").defaultSuccessUrl("/"))
                .logout(logoutConfig -> logoutConfig.logoutSuccessUrl("/"))
                .oauth2Login(oauth2LoginConfig -> oauth2LoginConfig.userInfoEndpoint(userInfoEndpointConfig ->
                        userInfoEndpointConfig.userService(customOAuth2UserService)));

        return http.build();
    }

    // 사용 x
//    @Bean
//    public InMemoryUserDetailsManager userDetailsService() {
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles(Role.USER.name())
//                .build();
//        return new InMemoryUserDetailsManager(user);
//    }
}
