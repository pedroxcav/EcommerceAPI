package com.ecommerce.api.service;

import com.ecommerce.api.exception.InvalidCPFException;
import com.ecommerce.api.exception.NullUserException;
import com.ecommerce.api.exception.UserRegisteredException;
import com.ecommerce.api.model.User;
import com.ecommerce.api.model.dto.user.AuthenticationDTO;
import com.ecommerce.api.model.dto.user.RegistrationDTO;
import com.ecommerce.api.model.dto.user.UserResponseDTO;
import com.ecommerce.api.model.enums.Role;
import com.ecommerce.api.repository.AddressRepository;
import com.ecommerce.api.repository.OrderRepository;
import com.ecommerce.api.repository.UserRepository;
import com.ecommerce.api.service.component.Validator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final Validator validator;
    private final AuthnService authnService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;
    private final AuthenticationManager authenticationManager;

    public UserService(AuthnService authnService, UserRepository userRepository, OrderRepository orderRepository, PasswordEncoder passwordEncoder, AddressRepository addressRepository, AuthenticationManager authenticationManager, Validator validator) {
        this.authnService = authnService;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.passwordEncoder = passwordEncoder;
        this.addressRepository = addressRepository;
        this.authenticationManager = authenticationManager;
        this.validator = validator;
    }

    public void register(RegistrationDTO data, Role role) {
        if (!validator.validate(data.CPF()))
            throw new InvalidCPFException();
        else if(userRepository.existsByUsernameOrCPF(data.username(), data.CPF()))
            throw new UserRegisteredException();
        var encoded = passwordEncoder.encode(data.password());
        userRepository.save(
                new User(data.name(), data.username(), data.CPF(), data.email(), encoded, role));
    }
    public String login(AuthenticationDTO data) {
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
                        user.getActiveAdresses(),
                        user.getActiveCart(),
                        user.getWishlist(),
                        user.getPurchases()))
                .collect(Collectors.toList());
    }
    public Optional<User> getAuthnUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if(principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return userRepository.loadByUsername(username);
        } else
            throw new NullUserException();
    }
    public void deleteUser(String username) {
        User user = (User) userRepository.findByUsername(username);
        if(user != null) {
            user.getWishlist().forEach(product -> product.getUsers().remove(user));
            user.getPurchases().forEach(purchase -> purchase.setUser(null));
            user.getCart().forEach(order -> {
                order.setUser(null);
                if(!order.isCompleted())
                    orderRepository.delete(order);
            });
            user.getAdresses().forEach(address -> {
                address.setUser(null);
                address.setActive(false);
                if (address.getPurchases().isEmpty())
                    addressRepository.delete(address);
            });
            userRepository.delete(user);
        } else
            throw new NullUserException();
    }
}
