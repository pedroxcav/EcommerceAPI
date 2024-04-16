package com.ecommerce.api.service;

import com.ecommerce.api.exception.InvalidCPFException;
import com.ecommerce.api.exception.NullUserException;
import com.ecommerce.api.exception.UserRegisteredException;
import com.ecommerce.api.model.*;
import com.ecommerce.api.model.dto.user.AuthenticationDTO;
import com.ecommerce.api.model.dto.user.RegistrationDTO;
import com.ecommerce.api.model.dto.user.UpdateDTO;
import com.ecommerce.api.model.dto.user.UserDTO;
import com.ecommerce.api.model.enums.Role;
import com.ecommerce.api.repository.AddressRepository;
import com.ecommerce.api.repository.OrderRepository;
import com.ecommerce.api.repository.UserRepository;
import com.ecommerce.api.service.component.Validator;
import org.junit.jupiter.api.DisplayName;
import org.springframework.security.core.Authentication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private Validator validator;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private AuthnService authnService;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Register Successfully")
    void register_successful() {
        var data = new RegistrationDTO(
                "Admin",
                "admin",
                "24512127801",
                "admin@gmail.com",
                "1234");
        var role = Role.ADMIN;

        when(validator.validate(data.CPF())).thenReturn(true);
        when(userRepository.existsByUsernameOrCPFOrEmail(data.username(), data.CPF(), data.email())).thenReturn(false);

        userService.register(data, role);

        verify(passwordEncoder, times(1)).encode(data.password());
        verify(userRepository, times(1)).save(any(User.class));
    }
    @Test
    @DisplayName("Register Unsuccessfully - Invalid CPF")
    void register_unsuccessful_case1() {
        var data = new RegistrationDTO(
                "Admin",
                "admin",
                "24512127801",
                "admin@gmail.com",
                "1234");
        var role = Role.ADMIN;

        when(validator.validate(data.CPF())).thenReturn(false);

        Assertions.assertThrows(InvalidCPFException.class, () -> userService.register(data, role));
    }
    @Test
    @DisplayName("Register Unsuccessfully - Already In")
    void register_unsuccessful_case2() {
        var data = new RegistrationDTO(
                "Admin",
                "admin",
                "24512127801",
                "admin@gmail.com",
                "1234");
        var role = Role.ADMIN;

        when(validator.validate(data.CPF())).thenReturn(true);
        when(userRepository.existsByUsernameOrCPFOrEmail(data.username(), data.CPF(), data.email())).thenReturn(true);

        Assertions.assertThrows(UserRegisteredException.class, () -> userService.register(data, role));
    }

    @Test
    @DisplayName("Login Successfully")
    void login_successful() {
        var data = new AuthenticationDTO("admin","1245");
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authnService.generateToken(any())).thenReturn("token");

        String token = userService.login(data);

        Assertions.assertEquals("token", token);
        verify(authenticationManager, times(1)).authenticate(any());
        verify(authnService, times(1)).generateToken(any());
    }

    @Test
    @DisplayName("Get All Users Successfully")
    void getAllUsers() {
        var firstUser = new User(
                "Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        firstUser.setWishlist(new HashSet<>());
        firstUser.setCart(new ArrayList<>());
        firstUser.setAdresses(new HashSet<>());
        firstUser.setPurchases(new ArrayList<>());
        var secondUser = new User(
                "Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        secondUser.setWishlist(new HashSet<>());
        secondUser.setCart(new ArrayList<>());
        secondUser.setAdresses(new HashSet<>());
        secondUser.setPurchases(new ArrayList<>());
        when(userRepository.findAll()).thenReturn(Arrays.asList(firstUser, secondUser));

        List<UserDTO> userDTOList = userService.getAllUsers();

        Assertions.assertEquals(2, userDTOList.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Delete Successfully")
    void deleteUser_successful() {
        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        user.setWishlist(new HashSet<>());
        user.setCart(new ArrayList<>());
        user.setAdresses(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        Assertions.assertDoesNotThrow(() -> userService.deleteUser(user.getUsername()));

        verify(userRepository, atLeastOnce()).delete(any(User.class));
        verify(addressRepository, never()).delete(any(Address.class));
        verify(orderRepository, never()).delete(any(Order.class));
    }
    @Test
    @DisplayName("Delete Unsuccessfully - NonExistent")
    void deleteUser_unsuccessful() {
        when(userRepository.findByUsername(any(String.class))).thenReturn(null);

        Assertions.assertThrows(NullUserException.class, () -> userService.deleteUser(any(String.class)));
    }

    @Test
    @DisplayName("Update Successfully")
    void updateUser_successful() {
        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        when(authnService.getAuthnUser()).thenReturn(Optional.of(user));
        when(userRepository.findAllByUsernameOrEmail(any(), any())).thenReturn(new HashSet<>());

        var data = new UpdateDTO(
                "New Name", "New Username",
                "New Email", "New Password");
        Assertions.assertDoesNotThrow(() -> userService.updateUser(data));
    }
    @Test
    @DisplayName("Update Unsuccessfully")
    void updateUser_unsuccessful() {
        var firstUser = new User("User", "user",
                "17719960807", "user@gmail.com",
                "1234", Role.USER);
        firstUser.setId(UUID.randomUUID());
        var secondUser = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        secondUser.setId(UUID.randomUUID());
        when(authnService.getAuthnUser()).thenReturn(Optional.of(firstUser));
        when(userRepository.findAllByUsernameOrEmail(any(), any())).thenReturn(Set.of(secondUser));

        var data = new UpdateDTO(
                "New Name", "admin",
                "admin@gmail.com", "New Password");
        Assertions.assertThrows(UserRegisteredException.class, () -> userService.updateUser(data));
    }
}