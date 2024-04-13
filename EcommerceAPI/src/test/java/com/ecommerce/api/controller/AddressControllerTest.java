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
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AddressControllerTest {
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
    @DisplayName("Register Address Successfully")
    void newAddress() throws Exception {
        String requestBody = """
                    {
                        "zipCode": "08500000",
                        "number": "00",
                        "street": "Street Name",
                        "neighborhood": "Neighborhood Name",
                        "city": "City Name",
                        "state": "State Name"
                    }
                """;
        mvc.perform(MockMvcRequestBuilders
                .post("/address/new")
                .header("Authorization", "Bearer " + authnService.generateToken(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get User Adresses Successfully")
    void getUserAdresses() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/address/registered")
                .header("Authorization", "Bearer " + authnService.generateToken(user)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Delete Successfully")
    void deleteAddress_successful_case01() throws Exception {
        var address = addressRepository.save(
                new Address("08532560", "Street Name",
                        "House Number", "Neighborhood Name",
                        "City Name", "State Name", user));
        mvc.perform(MockMvcRequestBuilders
                .delete("/address/delete/{id}", address.getId())
                .header("Authorization", "Bearer " + authnService.generateToken(user)))
                .andExpect(status().isOk());

        Optional<User> optionalUser = userRepository.loadByUsername(user.getUsername());
        optionalUser.ifPresent(userValue ->
                Assertions.assertEquals(userValue.getAdresses().size(), 0));
    }
    @Test
    @DisplayName("Delete Successfully - Without Purchase")
    void deleteAddress_successful_case02() throws Exception {
        var address = addressRepository.save(
                new Address("08532560", "Street Name",
                        "House Number", "Neighborhood Name",
                        "City Name", "State Name", user));
        var product = productRepository.save(new Product("Iphone", "Apple smartphone.",5000D));
        orderRepository.save(new Order(product, 5, product.getPrice() * 5, user));

        mvc.perform(MockMvcRequestBuilders
                .delete("/address/delete/{id}", address.getId())
                .header("Authorization", "Bearer " + authnService.generateToken(user)))
                .andExpect(status().isOk());

        Optional<User> optionalUser = userRepository.loadByUsername(user.getUsername());
        optionalUser.ifPresent(userValue ->
                Assertions.assertEquals(userValue.getAdresses().size(), 0));
    }
    @Test
    @DisplayName("Delete Successfully - With Dependency")
    void deleteAddress_successful_case03() throws Exception {
        var address = addressRepository.save(
                new Address("08532560", "Street Name",
                        "House Number", "Neighborhood Name",
                        "City Name", "State Name", user));
        var product = productRepository.save(new Product("Iphone", "Apple smartphone.",5000D));
        var order = new Order(product, 5, product.getPrice() * 5, user);
        order.setCompleted(true);
        orderRepository.save(order);
        purchaseRepository.save(new Purchase(Set.of(order), order.getPrice(), address, user));

        mvc.perform(MockMvcRequestBuilders
                .delete("/address/delete/{id}", address.getId())
                .header("Authorization", "Bearer " + authnService.generateToken(user)))
                .andExpect(status().isOk());

        Optional<User> optionalUser = userRepository.loadByUsername(user.getUsername());
        optionalUser.ifPresent(userValue -> {
            Assertions.assertEquals(userValue.getActiveAdresses().size(), 0);
            Assertions.assertEquals(userValue.getAdresses().size(), 1);
        });
    }
    @Test
    @DisplayName("Delete Unsuccessfully - Non Existent In List")
    void deleteAddress_unsuccessful_case01() throws Exception {
        var address = addressRepository.save(
                new Address("08532560", "Street Name",
                        "House Number", "Neighborhood Name",
                        "City Name", "State Name", admin));
        mvc.perform(MockMvcRequestBuilders
                .delete("/address/delete/{id}", address.getId())
                .header("Authorization", "Bearer " + authnService.generateToken(user)))
                .andExpect(status().isNotFound());
    }
    @Test
    @DisplayName("Delete Unsuccessfully - Non Existent")
    void deleteAddress_unsuccessful_case02() throws Exception {
        var address = mock(Address.class);
        mvc.perform(MockMvcRequestBuilders
                        .delete("/address/delete/{id}", address.getId())
                        .header("Authorization", "Bearer " + authnService.generateToken(user)))
                .andExpect(status().isNotFound());
    }
}