package com.ecommerce.api.controller;

import com.ecommerce.api.model.*;
import com.ecommerce.api.model.enums.Role;
import com.ecommerce.api.repository.*;
import com.ecommerce.api.service.AuthnService;
import org.junit.jupiter.api.Assertions;
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

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class OrderControllerTest {
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
    private ProductRepository productRepository;
    private static User user;
    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        user = userRepository.save(new User(
                "User", "user",
                "17719960807", "user@gmail.com",
                encoder.encode("1234"), Role.USER));
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Add Successfully")
    void addToCart_successful() throws Exception {
        String requestBody = """
                    {
                        "amount": 5,
                        "productId": 1
                    }
                """;
        productRepository.save(new Product("Iphone", "Apple smartphone.",5000D));
        mvc.perform(MockMvcRequestBuilders
                .post("/order/new")
                .header("Authorization", "Bearer " + authnService.generateToken(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());

        Optional<User> optionalUser = userRepository.loadByUsername(user.getUsername());
        optionalUser.ifPresent(userValue -> Assertions.assertEquals(1, userValue.getCart().size()));
    }
    @Test
    @DisplayName("Add Unsuccessfully - Invalid Amount")
    void addToCart_unsuccessful_case01() throws Exception {
        String requestBody = """
                    {
                        "amount": -5,
                        "productId": 1
                    }
                """;
        productRepository.save(new Product("Iphone", "Apple smartphone.",5000D));
        mvc.perform(MockMvcRequestBuilders
                .post("/order/new")
                .header("Authorization", "Bearer " + authnService.generateToken(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }
    @Test
    @DisplayName("Add Unsuccessfully - NonExistent Product")
    void addToCart_unsuccessful_case02() throws Exception {
        String requestBody = """
                    {
                        "amount": 5,
                        "productId": 1
                    }
                """;
        mvc.perform(MockMvcRequestBuilders
                .post("/order/new")
                .header("Authorization", "Bearer " + authnService.generateToken(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete Successfully")
    void deleteFromCart_successful() throws Exception {
        var product = productRepository.save(
                new Product("Iphone", "Apple smartphone.", 5000D));
        var order = orderRepository.save(
                new Order(product, 5, product.getPrice() * 5, user));
        mvc.perform(MockMvcRequestBuilders
                .delete("/order/delete/{id}", order.getId())
                .header("Authorization", "Bearer " + authnService.generateToken(user)))
                .andExpect(status().isOk());

        Optional<User> optionalUser = userRepository.loadByUsername(user.getUsername());
        optionalUser.ifPresent(userValue -> Assertions.assertEquals(0, userValue.getCart().size()));
    }
    @Test
    @DisplayName("Delete Unsuccessfully - NonExistent Order")
    void deleteFromCart_unsuccessful() throws Exception {
        var order = mock(Order.class);
        mvc.perform(MockMvcRequestBuilders
                        .delete("/order/delete/{id}", order.getId())
                        .header("Authorization", "Bearer " + authnService.generateToken(user)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get User Cart Successfully")
    void getUserCart() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/order/cart")
                .header("Authorization", "Bearer " + authnService.generateToken(user)))
                .andExpect(status().isOk());
    }
}