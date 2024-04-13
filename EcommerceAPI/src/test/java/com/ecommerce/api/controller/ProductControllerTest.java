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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProductControllerTest {
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
        admin = userRepository.save(new User(
                "Admin", "admin",
                "24512127801", "admin@gmail.com",
                encoder.encode("1234"), Role.ADMIN));
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Register Successfully")
    void newProduct_successful() throws Exception {
        String requestBody = """
                    {
                        "name": "Iphone",
                        "description": "Apple smartphone.",
                        "price": 5000
                    }
                """;
        mvc.perform(MockMvcRequestBuilders
                .post("/product/new")
                .header("Authorization", "Bearer " + authnService.generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("Register Unsuccessfully")
    void newProduct_unsuccessful() throws Exception {
        String requestBody = """
                    {
                        "name": "Iphone",
                        "description": "Apple smartphone.",
                        "price": 5000
                    }
                """;
        mvc.perform(MockMvcRequestBuilders
                .post("/product/new")
                .header("Authorization", "Bearer " + authnService.generateToken(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Get ALl Products Successfully")
    void getAllProducts() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/product/registered")
                .header("Authorization", "Bearer " + authnService.generateToken(user)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Delete Successfully")
    void deleteProduct_successful_case01() throws Exception {
        var product = productRepository.save(
                new Product("Iphone", "Smartphone apple.", 5000D));
        mvc.perform(MockMvcRequestBuilders
                .delete("/product/delete/{id}", product.getId())
                .header("Authorization", "Bearer " + authnService.generateToken(admin)))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("Delete Successfully - With Dependency")
    void deleteProduct_successful_case02() throws Exception {
        var product = productRepository.save(
                new Product("Iphone", "Smartphone apple.", 5000D));
        var order = new Order(product, 2, product.getPrice() * 2, user);
        order.setCompleted(true);
        orderRepository.save(order);
        var address = addressRepository.save(
                new Address("08532560", "Street Name",
                        "House Number", "Neighborhood Name",
                        "City Name", "State Name", user));
        purchaseRepository.save(new Purchase(Set.of(order), order.getPrice(), address, user));

        mvc.perform(MockMvcRequestBuilders
                        .delete("/product/delete/{id}", product.getId())
                        .header("Authorization", "Bearer " + authnService.generateToken(admin)))
                .andExpect(status().isOk());

        Optional<User> optionalUser = userRepository.loadByUsername(user.getUsername());
        optionalUser.ifPresent(userValue -> {
            Assertions.assertEquals(1, userValue.getCart().size());
            Assertions.assertEquals(0, userValue.getActiveCart().size());
        });
    }
    @Test
    @DisplayName("Delete Unsuccessfully")
    void deleteProduct_unsuccessful_case01() throws Exception {
        var product = mock(Product.class);
        mvc.perform(MockMvcRequestBuilders
                        .delete("/product/delete/{id}", product.getId())
                        .header("Authorization", "Bearer " + authnService.generateToken(user)))
                .andExpect(status().isForbidden());
    }
    @Test
    @DisplayName("Delete Unsuccessfully - NonExistent")
    void deleteProduct_unsuccessful_case02() throws Exception {
        var product = mock(Product.class);
        mvc.perform(MockMvcRequestBuilders
                        .delete("/product/delete/{id}", product.getId())
                        .header("Authorization", "Bearer " + authnService.generateToken(admin)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Favorite Successfully")
    void favorite_successful() throws Exception {
        var product = productRepository.save(
                new Product("Iphone", "Apple smartphone.", 5000D));
        mvc.perform(MockMvcRequestBuilders
                .post("/product/favorite/{id}", product.getId())
                .header("Authorization", "Bearer " + authnService.generateToken(user)))
                .andExpect(status().isOk());

        Optional<User> optionalUser = userRepository.loadByUsername(user.getUsername());
        optionalUser.ifPresent(userValue ->
                Assertions.assertEquals(1, userValue.getWishlist().size()));
    }
    @Test
    @DisplayName("Product Already Favorited")
    void favorite_unsuccessful_case01() throws Exception {
        var product = productRepository.save(
                new Product("Iphone", "Apple smartphone.", 5000D));
        product.setUsers(new HashSet<>());
        user.setWishlist(new HashSet<>());
        user.getWishlist().add(product);
        product.getUsers().add(user);
        productRepository.save(product);

        mvc.perform(MockMvcRequestBuilders
                .post("/product/favorite/{id}", product.getId())
                .header("Authorization", "Bearer " + authnService.generateToken(user)))
                .andExpect(status().isInternalServerError());
    }
    @Test
    @DisplayName("Favorite Unsuccessfully - NonExistent")
    void favorite_unsuccessful_case02() throws Exception {
        var product = mock(Product.class);
        mvc.perform(MockMvcRequestBuilders
                .post("/product/favorite/{id}", product.getId())
                .header("Authorization", "Bearer " + authnService.generateToken(user)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Unfavorite Successfully")
    void unfavorite_successful() throws Exception {
        var product = productRepository.save(
                new Product("Iphone", "Apple smartphone.", 5000D));
        product.setUsers(new HashSet<>());
        user.setWishlist(new HashSet<>());
        user.getWishlist().add(product);
        product.getUsers().add(user);
        productRepository.save(product);

        mvc.perform(MockMvcRequestBuilders
                .delete("/product/unfavorite/{id}", product.getId())
                .header("Authorization", "Bearer " + authnService.generateToken(user)))
                .andExpect(status().isOk());

        Optional<User> optionalUser = userRepository.loadByUsername(user.getUsername());
        optionalUser.ifPresent(userValue ->
                Assertions.assertTrue(userValue.getWishlist().isEmpty()));
    }
    @Test
    @DisplayName("Product Doesn't Favorited")
    void unfavorite_unsuccessful_case01() throws Exception {
        var product = productRepository.save(
                new Product("Iphone", "Apple smartphone.", 5000D));
        mvc.perform(MockMvcRequestBuilders
                        .delete("/product/unfavorite/{id}", product.getId())
                        .header("Authorization", "Bearer " + authnService.generateToken(user)))
                .andExpect(status().isInternalServerError());
    }
    @Test
    @DisplayName("Unfavorite Unsuccessfully - NonExistent")
    void unfavorite_unsuccessful_case02() throws Exception {
        var product = mock(Product.class);
        mvc.perform(MockMvcRequestBuilders
                        .delete("/product/unfavorite/{id}", product.getId())
                        .header("Authorization", "Bearer " + authnService.generateToken(user)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get User Wishlist Successfully")
    void getUserWishlist() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/product/wishlist")
                .header("Authorization", "Bearer " + authnService.generateToken(user)))
                .andExpect(status().isOk());
    }
}