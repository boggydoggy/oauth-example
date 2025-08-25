package com.altimedia.example.oauth.config.auth;

import com.altimedia.example.oauth.domain.Users;
import lombok.Getter;

@Getter
public class SessionUser {
    private final String name, email, picture;

    public SessionUser(Users users) {
        this.name = users.getName();
        this.email = users.getEmail();
        this.picture = users.getPicture();
    }
}
