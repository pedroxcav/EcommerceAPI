package com.ecommerce.api.service;

import com.ecommerce.api.model.User;
import com.ecommerce.api.model.dto.Authentication;
import com.ecommerce.api.model.dto.Registration;
import com.ecommerce.api.model.enums.Role;
import com.ecommerce.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    public User register(Registration data, Role role) {
        if(userRepository.findByUsername(data.username()) != null)
            throw new RuntimeException("User already exists in database!!");
        var encoded = passwordEncoder.encode(data.password());
        return userRepository.save(
                new User(
                        data.name(),
                        data.username(),
                        data.CPF(),
                        data.email(),
                        encoded,
                        role
                )
        );
    }
    public String login(Authentication data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.username(), data.password());
        var authentication = authenticationManager.authenticate(usernamePassword);
        String token = tokenService.generateToken((User) authentication.getPrincipal());
        return token;
    }
    public List<User> findAll() {
        return userRepository.findAll();
    }
}
