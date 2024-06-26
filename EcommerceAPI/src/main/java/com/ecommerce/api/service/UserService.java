package com.ecommerce.api.service;

import com.ecommerce.api.exception.InvalidCPFException;
import com.ecommerce.api.exception.NullUserException;
import com.ecommerce.api.exception.UserRegisteredException;
import com.ecommerce.api.model.User;
import com.ecommerce.api.model.dto.user.AuthenticationDTO;
import com.ecommerce.api.model.dto.user.RegistrationDTO;
import com.ecommerce.api.model.dto.user.UpdateDTO;
import com.ecommerce.api.model.dto.user.UserDTO;
import com.ecommerce.api.model.enums.Role;
import com.ecommerce.api.repository.AddressRepository;
import com.ecommerce.api.repository.OrderRepository;
import com.ecommerce.api.repository.UserRepository;
import com.ecommerce.api.service.component.Validator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final Validator validator;
    private final PasswordEncoder encoder;
    private final AuthnService authnService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final AuthenticationManager authenticationManager;

    public UserService(AuthnService authnService, UserRepository userRepository, OrderRepository orderRepository, PasswordEncoder encoder, AddressRepository addressRepository, AuthenticationManager authenticationManager, Validator validator) {
        this.encoder = encoder;
        this.validator = validator;
        this.authnService = authnService;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
        this.authenticationManager = authenticationManager;
    }

    public void register(RegistrationDTO data, Role role) {
        if (!validator.validate(data.CPF()))
            throw new InvalidCPFException();
        else if(userRepository.existsByUsernameOrCPFOrEmail(data.username(), data.CPF(), data.email()))
            throw new UserRegisteredException();
        var encoded = encoder.encode(data.password());
        userRepository.save(
                new User(data.name(), data.username(), data.CPF(), data.email(), encoded, role));
    }
    public String login(AuthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.username(), data.password());
        var authentication = authenticationManager.authenticate(usernamePassword);
        return authnService.generateToken((User) authentication.getPrincipal());
    }
    public List<UserDTO> getAllUsers() {
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .map(user -> new UserDTO(
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
    public void updateUser(UpdateDTO data) {
        Optional<User> optionalUser = authnService.getAuthnUser();
        optionalUser.ifPresent(user -> {
            var usedDataList = userRepository.findAllByUsernameOrEmail(data.username(), data.email());
            if (usedDataList.stream().anyMatch(userValue -> !userValue.getId().equals(user.getId())))
                throw new UserRegisteredException("Data already in use!");
            user.setName(data.name());
            user.setUsername(data.username());
            user.setEmail(data.email());
            user.setPassword(encoder.encode(data.password()));
            userRepository.save(user);
        });
    }
}
