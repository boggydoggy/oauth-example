package com.altimedia.example.oauth.service;

import com.altimedia.example.oauth.config.auth.SessionUser;
import com.altimedia.example.oauth.domain.Users;
import com.altimedia.example.oauth.dto.OAuthAttributes;
import com.altimedia.example.oauth.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 현재 로그인 진행 중인 서비스 구분(google, naver, kakao, etc...)
        String registrationId = userRequest
                .getClientRegistration()
                .getRegistrationId();
        log.info("registrationId: {}", registrationId);

        // OAuth2 로그인 진행 시 키 값(각 소셜 서비스의 유니크 필드)
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
        log.info("userNameAttributeName: {}", userNameAttributeName);

        // OAuth 로그인을 통해 가져온 OAuth2User의 attribute를 담아주는 of 메소드 (Dto로 취급)
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
        Users users = saveOrUpdate(attributes);

        // 사용자 정보를 Session에 저장.
        // SessionUser는 Serializable한 Dto
        httpSession.setAttribute("user", new SessionUser(users));

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(users.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()
        );
    }

    private Users saveOrUpdate(OAuthAttributes attributes) {
        List<Users> result = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .toList();

        Users users;

        if (result.isEmpty()) {
            users = attributes.toEntity();
        } else {
            users = result.get(0);
        }

        return userRepository.save(users);
    }
}
