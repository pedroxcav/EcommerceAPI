package com.ecommerce.api.service;

import com.ecommerce.api.exception.InvalidCPFException;
import com.ecommerce.api.exception.NullUserException;
import com.ecommerce.api.exception.UserRegisteredException;
import com.ecommerce.api.model.*;
import com.ecommerce.api.model.dto.user.AuthenticationDTO;
import com.ecommerce.api.model.dto.user.RegistrationDTO;
import com.ecommerce.api.model.dto.user.UserResponseDTO;
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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
        when(userRepository.existsByUsernameOrCPF(data.username(), data.CPF())).thenReturn(false);

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
        when(userRepository.existsByUsernameOrCPF(data.username(), data.CPF())).thenReturn(true);

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

        List<UserResponseDTO> userResponseDTOList = userService.getAllUsers();

        Assertions.assertEquals(2, userResponseDTOList.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Get Authenticated User Successfully")
    void getAuthUser() {
        User user = new User();
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        String username = "admin";
        when(userDetails.getUsername()).thenReturn(username);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.loadByUsername(username)).thenReturn(Optional.of(user));

        Optional<User> actualUser = userService.getAuthnUser();
        Assertions.assertEquals(Optional.of(user), actualUser);
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

        var mockedHashset = mock(HashSet.class);
        var mockedPurchase = mock(Purchase.class);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        Assertions.assertDoesNotThrow(() -> userService.deleteUser(user.getUsername()));

        verify(userRepository, atLeastOnce()).delete(any(User.class));
        verify(addressRepository, never()).delete(any(Address.class));
        verify(orderRepository, never()).delete(any(Order.class));
        verify(mockedHashset, never()).remove(any(User.class));
        verify(mockedPurchase, never()).setUser(null);
    }
    @Test
    @DisplayName("Delete Unsuccessfully - NonExistent")
    void deleteUser_unsuccessful() {
        when(userRepository.findByUsername(any(String.class))).thenReturn(null);

        Assertions.assertThrows(NullUserException.class, () -> userService.deleteUser(any(String.class)));
    }
}