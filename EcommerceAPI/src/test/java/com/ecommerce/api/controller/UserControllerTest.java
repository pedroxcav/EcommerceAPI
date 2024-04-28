package com.ecommerce.api.controller;

import com.ecommerce.api.exception.NullUserException;
import com.ecommerce.api.model.*;
import com.ecommerce.api.model.Number;
import com.ecommerce.api.model.enums.Role;
import com.ecommerce.api.repository.*;
import com.ecommerce.api.service.AuthnService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private AuthnService authnService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private NumberRepository numberRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PurchaseRepository purchaseRepository;
    private static User user;
    private static User admin;
    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        user = userRepository.save(new User(
                "User", "user",
                "17719960807", "user@gmail.com",
                encoder.encode("1234"), Role.USER));
        admin = userRepository.loadByUsername("admin")
                .orElseThrow(NullUserException::new);
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Register User Successfully")
    void registerUser_successful() throws Exception {
        String requestBody = """
                    {
                        "name": "User Name",
                        "username": "username",
                        "CPF": "13441689810",
                        "email": "useremail@gmail.com",
                        "password": "1234"
                    }
                """;
        mvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("Register Unsuccessfully - Invalid CPF")
    void registerUser_unsuccessful_case01() throws Exception {
        String requestBody = """
                    {
                        "name": "User Name",
                        "username": "username",
                        "CPF": "01010101010",
                        "email": "useremail@gmail.com",
                        "password": "1234"
                    }
                """;
        mvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }
    @Test
    @DisplayName("Register Unsuccessfully - Already In")
    void registerUser_unsuccessful_case02() throws Exception {
        String requestBody = """
                    {
                        "name": "User Name",
                        "username": "username",
                        "CPF": "13441689810",
                        "email": "useremail@gmail.com",
                        "password": "1234"
                    }
                """;
        userRepository.save(
                new User("User Name", "username",
                "13441689810", "useremail@gmail.com",
                encoder.encode("1234"), Role.USER));
        mvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isInternalServerError());
    }
    @Test
    @DisplayName("Register Admin Successfully")
    void registerAdmin_successful() throws Exception {
        String requestBody = """
                    {
                        "name": "User Name",
                        "username": "username",
                        "CPF": "13441689810",
                        "email": "useremail@gmail.com",
                        "password": "1234"
                    }
                """;
        mvc.perform(MockMvcRequestBuilders
                .post("/users/admin")
                .header("Authorization", "Bearer " + authnService.generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("Register Admin Unsuccessfully")
    void registerAdmin_unsuccessful() throws Exception {
        String requestBody = """
                    {
                        "name": "User Name",
                        "username": "username",
                        "CPF": "13441689810",
                        "email": "useremail@gmail.com",
                        "password": "1234"
                    }
                """;
        mvc.perform(MockMvcRequestBuilders
                .post("/users/admin")
                .header("Authorization", "Bearer " + authnService.generateToken(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Login Successfully")
    void login_successful() throws Exception {
        String requestBody = """
                    {
                        "username": "username",
                        "password": "1234"
                    }
                """;
        userRepository.save(
                new User("User Name", "username",
                "13441689810", "useremail@gmail.com",
                encoder.encode("1234"), Role.USER));
        mvc.perform(MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("Login Unsuccessfully")
    void login_unsuccessful() throws Exception {
        String requestBody = """
                    {
                        "username": "username",
                        "password": "1234"
                    }
                """;
        mvc.perform(MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Get All Users Successfully")
    void getAllUsers_successful() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/users")
                .header("Authorization", "Bearer " + authnService.generateToken(admin)))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("Get All Users Unsuccessfully")
    void getAllUsers_unsuccessful() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/users")
                        .header("Authorization", "Bearer " + authnService.generateToken(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Delete Successfully")
    void deleteUser_successful_case01() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .delete("/users/{username}", user.getUsername())
                .header("Authorization", "Bearer " + authnService.generateToken(admin)))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("Delete Successfully - Without Purchase")
    void deleteUser_successful_case02() throws Exception {
        addressRepository.save(new Address(
                "08532560", "Street Name", "House Number",
                "Neighborhood Name", "City Name", "State Name", user));
        var product = productRepository.save(
                new Product("Iphone", "Apple smartphone.", 5000D));
        product.setUsers(new HashSet<>());
        user.setWishlist(new HashSet<>());
        user.getWishlist().add(product);
        product.getUsers().add(user);
        productRepository.save(product);
        numberRepository.save(new Number("11", "910000000", user));
        orderRepository.save(new Order(product, 5, product.getPrice() * 5, user));

        mvc.perform(MockMvcRequestBuilders
                .delete("/users/{username}", user.getUsername())
                .header("Authorization", "Bearer " + authnService.generateToken(admin)))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("Delete Successfully - With Dependency")
    void deleteUser_successful_case03() throws Exception {
        var address = addressRepository.save(new Address(
                "08532560", "Street Name", "House Number",
                "Neighborhood Name", "City Name", "State Name", user));
        var product = productRepository.save(
                new Product("Iphone", "Apple smartphone.", 5000D));
        product.setUsers(new HashSet<>());
        user.setWishlist(new HashSet<>());
        user.getWishlist().add(product);
        product.getUsers().add(user);
        productRepository.save(product);
        numberRepository.save(new Number("11", "910000000", user));
        var order = new Order(product, 5, product.getPrice() * 5, user);
        order.setCompleted(true);
        orderRepository.save(order);
        purchaseRepository.save(new Purchase(Set.of(order), order.getPrice(), address, user));
        mvc.perform(MockMvcRequestBuilders
                .delete("/users/{username}", user.getUsername())
                .header("Authorization", "Bearer " + authnService.generateToken(admin)))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("Delete Unsuccessfully - NonExistent")
    void deleteUser_unsuccessful_case01() throws Exception {
        var user = mock(User.class);
        mvc.perform(MockMvcRequestBuilders
                .delete("/users/{username}", user.getUsername())
                .header("Authorization", "Bearer " + authnService.generateToken(admin)))
                .andExpect(status().isNotFound());
    }
    @Test
    @DisplayName("Delete Unsuccessfully")
    void deleteUser_unsuccessful_case02() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .delete("/users/{username}", user.getUsername())
                .header("Authorization", "Bearer " + authnService.generateToken(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Update Successfully")
    void updateUser_successful() throws Exception {
        String requestBody = """
                    {
                        "name": "New Name",
                        "username": "New Username",
                        "email": "New Email",
                        "password": "New Password"
                    }
                """;
        mvc.perform(MockMvcRequestBuilders
                .put("/users")
                .header("Authorization", "Bearer " + authnService.generateToken(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("Update Unsuccessfully")
    void updateUser_unsuccessful() throws Exception {
        String requestBody = """
                    {
                        "name": "New Name",
                        "username": "New Username",
                        "email": "admin@gmail.com",
                        "password": "New Password"
                    }
                """;
        mvc.perform(MockMvcRequestBuilders
                .put("/users")
                .header("Authorization", "Bearer " + authnService.generateToken(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isInternalServerError());
    }
}