package com.altimedia.example.oauth.config.auth;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ManualOAuthConfig {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String authorizationUri;
    private String tokenUri;
    private String userInfoUri;
    private String scope;
}
