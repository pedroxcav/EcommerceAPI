package com.ecommerce.api.repository;

import com.ecommerce.api.model.User;
import com.ecommerce.api.model.dto.user.RegistrationDTO;
import com.ecommerce.api.model.enums.Role;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
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
    void loadByUsername_successful() {
        var data = new RegistrationDTO(
                "Pedro Cavalcanti",
                "pedroxcav",
                "01010101010",
                "pedroxcav@icloud.com",
                "1234");
        this.register(data);
        Optional<User> optionalUser = this.userRepository.loadByUsername(data.username());
        Assertions.assertTrue(optionalUser.isPresent());
        Assertions.assertEquals(data.username(), optionalUser.get().getUsername());
    }
    @Test
    void loadByUsername_unsuccessful() {
        Optional<User> optionalUser = this.userRepository.loadByUsername("pedroxcav");
        Assertions.assertTrue(optionalUser.isEmpty());
    }

    private void register(RegistrationDTO data) {
        var user = new User(
                data.name(), data.username(),
                data.CPF(), data.email(),
                data.password(), Role.ADMIN);
        entityManager.persist(user);
    }
}