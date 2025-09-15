package com.altimedia.example.oauth.service;

import com.altimedia.example.oauth.domain.Users;
import com.altimedia.example.oauth.dto.OAuthAttributes;
import com.altimedia.example.oauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CommonOAuthService {
    private final UserRepository userRepository;

    public Users saveOrUpdate(OAuthAttributes attributes) {
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
