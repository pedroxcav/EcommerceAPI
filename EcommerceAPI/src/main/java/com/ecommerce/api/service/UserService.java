package com.ecommerce.api.service;

import com.ecommerce.api.model.User;
import com.ecommerce.api.model.dto.Registration;
import com.ecommerce.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    public User register(Registration data) {
        return userRepository.save(new User(data.name(), data.username(), data.CPF(), data.email(), data.password(), data.role()));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
