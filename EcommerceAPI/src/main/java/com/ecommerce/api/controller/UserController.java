package com.ecommerce.api.controller;

import com.ecommerce.api.model.User;
import com.ecommerce.api.model.dto.Registration;
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

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid Registration data) {
        userService.register(data);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/registered")
    public ResponseEntity getRegisteredUsers() {
        List<User> registeredUsers = userService.findAll();
        return ResponseEntity.ok(registeredUsers);
    }
}
