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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrfConfig -> csrfConfig.disable())
                .headers(headersConfig -> headersConfig.frameOptions(frameOptionsConfig ->
                        frameOptionsConfig.disable()))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/", "/Savory-gh-pages/**").permitAll())
                .formLogin(formLoginConfig -> formLoginConfig.loginPage("/login").defaultSuccessUrl("/"))
                .logout(logoutConfig -> logoutConfig.logoutSuccessUrl("/"))
                .oauth2Login(oauth2LoginConfig -> oauth2LoginConfig.userInfoEndpoint(userInfoEndpointConfig ->
                        userInfoEndpointConfig.userService(customOAuth2UserService)));

        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles(Role.USER.name())
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}
