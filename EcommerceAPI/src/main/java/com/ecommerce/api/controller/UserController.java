package com.ecommerce.api.controller;

import com.ecommerce.api.model.dto.user.AthenticationDTO;
import com.ecommerce.api.model.dto.user.RegistrationDTO;
import com.ecommerce.api.model.dto.user.UserResponseDTO;
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

    @PostMapping("/register")
    public ResponseEntity registerUser(@RequestBody @Valid RegistrationDTO data) {
        userService.register(data, Role.USER);
        return ResponseEntity.ok("Registred");
    }
    @PostMapping("/register/admin")
    public ResponseEntity registerAdmin(@RequestBody @Valid RegistrationDTO data) {
        userService.register(data, Role.ADMIN);
        return ResponseEntity.ok("Registred");
    }
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AthenticationDTO data) {
        String token = userService.login(data);
        return ResponseEntity.ok(token);
    }
    @GetMapping("/registered")
    public ResponseEntity getAllUsers() {
        List<UserResponseDTO> registrationDTOList = userService.getAllUsers();
        return ResponseEntity.ok(registrationDTOList);
    }
    @DeleteMapping("/delete/{username}")
    public ResponseEntity deleteUser(@PathVariable(name = "username") String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok("Deleted");
    }
}
