package com.ecommerce.api.controller;

import com.ecommerce.api.exception.NullUserException;
import com.ecommerce.api.model.*;
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

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PurchaseControllerTest {
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
    private AddressRepository addressRepository;
    @Autowired
    private ProductRepository productRepository;
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
    @DisplayName("Buy Order Successfully")
    void buyOrders_successful() throws Exception {
        String requestBody = """
                    {
                        "orderIdSet": [1],
                        "addressId": 1
                    }
                """;
        var product = productRepository.save(
                new Product("Iphone", "Apple smartphone.", 5000D));
        addressRepository.save(new Address("08532560", "Street Name",
                        "House Number", "Neighborhood Name",
                        "City Name", "State Name", user));
        orderRepository.save(new Order(product, 2, product.getPrice() * 2, user));
        mvc.perform(MockMvcRequestBuilders
                .post("/purchases")
                .header("Authorization", "Bearer " + authnService.generateToken(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("Unsuccessfully - Address NonExistent")
    void buyOrders_unsuccessful_case01() throws Exception {
        String requestBody = """
                    {
                        "orderIdSet": [1],
                        "addressId": 1
                    }
                """;
        var product = productRepository.save(
                new Product("Iphone", "Apple smartphone.", 5000D));
        orderRepository.save(new Order(product, 2, product.getPrice() * 2, user));
        mvc.perform(MockMvcRequestBuilders
                .post("/purchases")
                .header("Authorization", "Bearer " + authnService.generateToken(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound());

    }
    @Test
    @DisplayName("Unsuccessfully - Order NonExistent")
    void buyOrders_unsuccessful_case02() throws Exception {
        String requestBody = """
                    {
                        "orderIdSet": [1],
                        "addressId": 1
                    }
                """;
        productRepository.save(new Product("Iphone", "Apple smartphone.", 5000D));
        addressRepository.save(new Address("08532560", "Street Name",
                "House Number", "Neighborhood Name",
                "City Name", "State Name", user));
        mvc.perform(MockMvcRequestBuilders
                        .post("/purchases")
                        .header("Authorization", "Bearer " + authnService.generateToken(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get User Purchases Successfully")
    void getUserPurchases() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/purchases/me")
                .header("Authorization", "Bearer " + authnService.generateToken(user)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get All Purchases Successfully")
    void getAllPurchases_successful() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/purchases")
                        .header("Authorization", "Bearer " + authnService.generateToken(admin)))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("Get All Purchases Unsuccessfully")
    void getAllPurchases_unsuccessful() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/purchases")
                        .header("Authorization", "Bearer " + authnService.generateToken(user)))
                .andExpect(status().isForbidden());
    }
}