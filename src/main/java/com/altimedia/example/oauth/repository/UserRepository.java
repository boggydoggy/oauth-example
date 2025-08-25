package com.altimedia.example.oauth.repository;

import com.altimedia.example.oauth.domain.Users;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public class UserRepository {
    private final EntityManager entityManager;

    public UserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Stream<Users> findByEmail(String email) {
        List<Users> result = entityManager.createQuery("select U from Users U where U.email = :email", Users.class)
                .setParameter("email", email)
                .getResultList();
        if (result.isEmpty()) {
            // 예외
        }
        return result.stream();
    }

    public Users save(Users users) {
        entityManager.persist(users);

        return users;
    }
}
