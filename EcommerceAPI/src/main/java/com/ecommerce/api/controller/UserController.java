package com.ecommerce.api.controller;

import com.ecommerce.api.model.dto.user.AuthenticationDTO;
import com.ecommerce.api.model.dto.user.RegistrationDTO;
import com.ecommerce.api.model.dto.user.UserDTO;
import com.ecommerce.api.model.dto.user.UpdateDTO;
import com.ecommerce.api.model.enums.Role;
import com.ecommerce.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> registerUser(@RequestBody @Valid RegistrationDTO data) {
        userService.register(data, Role.USER);
        return ResponseEntity.ok("Registered");
    }
    @PostMapping("/admin")
    public ResponseEntity<String> registerAdmin(@RequestBody @Valid RegistrationDTO data) {
        userService.register(data, Role.ADMIN);
        return ResponseEntity.ok("Registered");
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid AuthenticationDTO data) {
        String token = userService.login(data);
        return ResponseEntity.ok(token);
    }
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        var registrationDTOList = userService.getAllUsers();
        return ResponseEntity.ok(registrationDTOList);
    }
    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable(name = "username") String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok("Deleted");
    }

    @PutMapping
    public ResponseEntity<String> updateUser(@RequestBody @Valid UpdateDTO data) {
        userService.updateUser(data);
        return ResponseEntity.ok("Log in again!");
    }
}
