package com.ecommerce.api.service;

import com.ecommerce.api.model.User;
import com.ecommerce.api.model.dto.user.AthenticationDTO;
import com.ecommerce.api.model.dto.user.RegistrationDTO;
import com.ecommerce.api.model.dto.user.UserResponseDTO;
import com.ecommerce.api.model.enums.Role;
import com.ecommerce.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private AuthnService authnService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    public void register(RegistrationDTO data, Role role) {
        if(userRepository.findByUsername(data.username()) != null)
            throw new RuntimeException("User already exists in database!!");
        var encoded = passwordEncoder.encode(data.password());
        userRepository.save(
                new User(data.name(), data.username(), data.CPF(), data.email(), encoded, role)
        );
    }
    public String login(AthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.username(), data.password());
        var authentication = authenticationManager.authenticate(usernamePassword);
        return authnService.generateToken((User) authentication.getPrincipal());
    }
    public List<UserResponseDTO> getAllUsers() {
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getName(),
                        user.getUsername(),
                        user.getCPF(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getRole(),
                        user.getNumber(),
                        user.getAdresses(),
                        user.getCart(),
                        user.getWishlist(),
                        user.getPurchases()))
                .collect(Collectors.toList());
    }
    public UserResponseDTO getAuthUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        var user = (User) userRepository.findByUsername(principal.getUsername());
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getCPF(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.getNumber(),
                user.getAdresses(),
                user.getCart(),
                user.getWishlist(),
                user.getPurchases()
        );
    }
    public void deleteUser(String username) {
        User user = (User) userRepository.findByUsername(username);
        if(user == null)
            throw new RuntimeException("User doesn't exist in database!!");
        userRepository.delete(user);
    }
}
