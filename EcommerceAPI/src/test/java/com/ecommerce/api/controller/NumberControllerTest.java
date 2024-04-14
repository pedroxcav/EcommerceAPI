package com.ecommerce.api.controller;

import com.ecommerce.api.model.Number;
import com.ecommerce.api.model.User;
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

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class NumberControllerTest {
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private AuthnService authnService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private NumberRepository numberRepository;
    private static User user;
    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        user = userRepository.save(
                new User("User", "user",
                "17719960807", "user@gmail.com",
                encoder.encode("1234"), Role.USER));
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Register Number Successfully")
    void newNumber_successful() throws Exception {
        String requestBody = """
                    {
                        "areaCode": "11",
                        "number": "910000000"
                    }
                """;
        mvc.perform(MockMvcRequestBuilders
                .post("/numbers")
                .header("Authorization", "Bearer " + authnService.generateToken(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());

        Optional<User> optionalUser = userRepository.loadByUsername(user.getUsername());
        optionalUser.ifPresent(userValue -> Assertions.assertNotNull(userValue.getNumber()));
    }
    @Test
    @DisplayName("Number Already Registered")
    void newNumber_unsuccessful() throws Exception {
        String requestBody = """
                    {
                        "areaCode": "11",
                        "number": "910000000"
                    }
                """;
        numberRepository.save(new Number("11", "910000000", user));
        mvc.perform(MockMvcRequestBuilders
                .post("/numbers")
                .header("Authorization", "Bearer " + authnService.generateToken(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Delete Successfully")
    void deleteNumber_successful() throws Exception {
        numberRepository.save(new Number("11", "910000000", user));
        mvc.perform(MockMvcRequestBuilders
                .delete("/numbers")
                .header("Authorization", "Bearer " + authnService.generateToken(user)))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("Delete Unsuccessfully - NonExistent")
    void deleteNumber_unsuccessful() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .delete("/numbers")
                .header("Authorization", "Bearer " + authnService.generateToken(user)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update Successfully")
    void updateNumber_successful() throws Exception {
        String requestBody = """
                    {
                        "areaCode": "11",
                        "number": "920000000"
                    }
                """;
        numberRepository.save(new Number("11", "910000000", user));
        mvc.perform(MockMvcRequestBuilders
                .put("/numbers")
                .header("Authorization", "Bearer " + authnService.generateToken(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("Update Unsuccessfully - NonExistent")
    void updateNumber_unsuccessful() throws Exception {
        String requestBody = """
                    {
                        "areaCode": "11",
                        "number": "920000000"
                    }
                """;
        mvc.perform(MockMvcRequestBuilders
                .put("/numbers")
                .header("Authorization", "Bearer " + authnService.generateToken(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get User Number Successfully")
    void getUserNumber_successful() throws Exception {
        numberRepository.save(new Number("11", "910000000", user));
        mvc.perform(MockMvcRequestBuilders
                .get("/numbers/me")
                .header("Authorization", "Bearer " + authnService.generateToken(user)))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("Get User Number Unsuccessfully - NonExistent")
    void getUserNumber_unsuccessful() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/number/me")
                .header("Authorization", "Bearer " + authnService.generateToken(user)))
                .andExpect(status().isNotFound());
    }
}