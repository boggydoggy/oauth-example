package com.altimedia.example.oauth.service;

import com.altimedia.example.oauth.config.auth.ManualOAuthConfig;
import com.altimedia.example.oauth.config.auth.SessionUser;
import com.altimedia.example.oauth.domain.Users;
import com.altimedia.example.oauth.dto.OAuthAttributes;
import com.altimedia.example.oauth.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Transactional
@Service
public class ManualOAuthService extends CommonOAuthService{
    private final HttpSession httpSession;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, ManualOAuthConfig> oAuthConfigs = new HashMap<>();
    private final Map<String, String> userNameAttributes = new HashMap<>();

    // Google OAuth 설정
    @Value("${manual.oauth.google.client-id}")
    private String googleClientId;

    @Value("${manual.oauth.google.client-secret}")
    private String googleClientSecret;

    @Value("${manual.oauth.google.redirect-uri}")
    private String googleRedirectUri;

    // Naver OAuth 설정
    @Value("${manual.oauth.naver.client-id}")
    private String naverClientId;

    @Value("${manual.oauth.naver.client-secret}")
    private String naverClientSecret;

    @Value("${manual.oauth.naver.redirect-uri}")
    private String naverRedirectUri;

    // Kakao OAuth 설정
    @Value("${manual.oauth.kakao.client-id}")
    private String kakaoClientId;

    @Value("${manual.oauth.kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${manual.oauth.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    public ManualOAuthService(UserRepository userRepository, HttpSession httpSession) {
        super(userRepository);
        this.httpSession = httpSession;
    }

    @PostConstruct
    public void init() {
        userNameAttributes.put("google", "sub");
        userNameAttributes.put("naver", "response");
        userNameAttributes.put("kakao", "id");

        String oAuthScope = "profile email";

        oAuthConfigs.put("google", ManualOAuthConfig.builder()
                .clientId(googleClientId)
                .clientSecret(googleClientSecret)
                .redirectUri(googleRedirectUri)
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v2/userinfo")
                .scope(oAuthScope)
                .build());
        oAuthConfigs.put("naver", ManualOAuthConfig.builder()
                .clientId(naverClientId)
                .clientSecret(naverClientSecret)
                .redirectUri(naverRedirectUri)
                .authorizationUri("https://nid.naver.com/oauth2.0/authorize")
                .tokenUri("https://nid.naver.com/oauth2.0/token")
                .userInfoUri("https://openapi.naver.com/v1/nid/me")
                .scope(oAuthScope)
                .build());
        oAuthConfigs.put("kakao", ManualOAuthConfig.builder()
                .clientId(kakaoClientId)
                .clientSecret(kakaoClientSecret)
                .redirectUri(kakaoRedirectUri)
                .authorizationUri("https://kauth.kakao.com/oauth/authorize")
                .tokenUri("https://kauth.kakao.com/oauth/token")
                .userInfoUri("https://kapi.kakao.com/v2/user/me")
                .build());
    }

    // OAuth URL 생성
    public String getAuthUrl(String registrationId) {
        ManualOAuthConfig config = oAuthConfigs.get(registrationId);
        if (config == null) {
            throw new IllegalArgumentException("Unsupported OAuth provider: " + registrationId);
        }

        return UriComponentsBuilder
                .fromUriString(config.getAuthorizationUri())
                .queryParam("client_id", config.getClientId())
                .queryParam("redirect_uri", config.getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", config.getScope())
                .build()
                .toUriString();
    }

    // OAuth 콜백 처리
    public Users processCallback(String registrationId, String code) throws Exception {
        ManualOAuthConfig config = oAuthConfigs.get(registrationId);
        if (config == null) {
            throw new IllegalArgumentException("Unsupported OAuth provider: " + registrationId);
        }

        String accessToken = getAccessToken(config, code);

        Map rawUserInfo = getUserInfo(config, accessToken);

        String userNameAttributeName = userNameAttributes.get(registrationId);
        log.info("userNameAttributeName: {}", userNameAttributeName);

        log.info("attributes: {}", rawUserInfo);

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, rawUserInfo);
        return saveOrUpdate(attributes);
    }

    // Access Token 획득
    private String getAccessToken(ManualOAuthConfig config, String code) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", config.getClientId());
        params.add("client_secret", config.getClientSecret());
        params.add("code", code);
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", config.getRedirectUri());

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.exchange(config.getTokenUri(), HttpMethod.POST, request, Map.class);

        Map tokenMap = response.getBody();
        return (String) tokenMap.get("access_token");
    }

    // 사용자 정보 획득
    private Map getUserInfo(ManualOAuthConfig config, String accessToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Map> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(config.getUserInfoUri(), HttpMethod.GET, request, Map.class);

        Map bodyMap = response.getBody();
        log.info("bodyMap: {}", bodyMap);

        return bodyMap;
    }
}
