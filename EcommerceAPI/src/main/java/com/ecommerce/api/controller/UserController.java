package com.ecommerce.api.controller;

import com.ecommerce.api.model.User;
import com.ecommerce.api.model.dto.Authentication;
import com.ecommerce.api.model.dto.Registration;
import com.ecommerce.api.model.enums.Role;
import com.ecommerce.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register/user")
    public ResponseEntity registerUser(@RequestBody @Valid Registration data) {
        userService.register(data, Role.USER);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/register/admin")
    public ResponseEntity registerAdmin(@RequestBody @Valid Registration data) {
        userService.register(data, Role.ADMIN);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid Authentication data) {
        String token = userService.login(data);
        return ResponseEntity.ok(token);
    }
    @GetMapping("/registered")
    public ResponseEntity getRegisteredUsers() {
        List<User> registeredUsers = userService.findAll();
        return ResponseEntity.ok(registeredUsers);
    }
}
