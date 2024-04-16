package com.ecommerce.api.repository;

import com.ecommerce.api.model.User;
import com.ecommerce.api.model.enums.Role;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Load User Successfully")
    void loadByUsername_successful() {
        var user = new User("User", "user",
                        "17719960807", "user@gmail.com",
                        "1234", Role.USER);
        entityManager.persist(user);
        Optional<User> optionalUser = this.userRepository.loadByUsername(user.getUsername());
        Assertions.assertTrue(optionalUser.isPresent());
        Assertions.assertEquals(user.getUsername(), optionalUser.get().getUsername());
    }
    @Test
    @DisplayName("Load User Unsuccessfully")
    void loadByUsername_unsuccessful() {
        Optional<User> optionalUser = this.userRepository.loadByUsername("admin");
        Assertions.assertTrue(optionalUser.isEmpty());
    }
}