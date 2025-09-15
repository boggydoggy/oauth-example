package com.altimedia.example.oauth.config.auth;

import com.altimedia.example.oauth.domain.Users;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {
    private final String name, email, picture, provider;

    public SessionUser(Users users) {
        this.name = users.getName();
        this.email = users.getEmail();
        this.picture = users.getPicture();
        this.provider = users.getProvider();
    }
}
