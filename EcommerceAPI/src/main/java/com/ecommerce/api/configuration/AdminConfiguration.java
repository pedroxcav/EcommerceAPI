package com.ecommerce.api.configuration;

import com.ecommerce.api.model.User;
import com.ecommerce.api.model.enums.Role;
import com.ecommerce.api.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
public class AdminConfiguration implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public AdminConfiguration(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        var user = new User(
                "Administrator", "admin",
                "00000000000", "admin@gmail.com",
                encoder.encode("1234"), Role.ADMIN);
        Optional<User> optionalUser = userRepository.loadByUsername(user.getUsername());
        optionalUser.ifPresentOrElse(
                userValue -> System.out.printf(
                        "-> Admin logged in: User(Name: %s, Username: %s, Role: %s)%n",
                        userValue.getName(), userValue.getUsername(), userValue.getRole()),
                () -> userRepository.save(user));
    }
}
