package com.ecommerce.api.service;

import com.ecommerce.api.model.Address;
import com.ecommerce.api.model.User;
import com.ecommerce.api.model.dto.AthenticationDTO;
import com.ecommerce.api.model.dto.RegistrationDTO;
import com.ecommerce.api.model.dto.UserDTO;
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
    public List<UserDTO> getAllUsers() {
        List<User> userList = userRepository.findAll();
        return userList
                .stream()
                .map(user -> new UserDTO(
                        user.getName(),
                        user.getUsername(),
                        user.getCPF(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getRole(),
                        user.getNumber(),
                        user.getAddress()
                                .stream()
                                .filter(Address::isActive)
                                .collect(Collectors.toSet()),
                        user.getCart(),
                        user.getWishlist(),
                        user.getPurchases()))
                .collect(Collectors.toList());
    }
    public String getAuthnUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        return principal.getUsername();
    }
    public void deleteUser(String username) {
        User user = (User) userRepository.findByUsername(username);
        if(user == null)
            throw new RuntimeException("User doesn't exist in database!!");
        userRepository.delete(user);
    }
}
