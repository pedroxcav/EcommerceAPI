package com.ecommerce.api.controller;

import com.ecommerce.api.model.dto.number.NumberRequestDTO;
import com.ecommerce.api.model.dto.number.NumberResponseDTO;
import com.ecommerce.api.service.NumberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/number")
public class NumberController {
    @Autowired
    private NumberService numberService;

    @PostMapping("/new")
    public ResponseEntity newNumber(@RequestBody @Valid NumberRequestDTO data) {
        numberService.newNumber(data);
        return ResponseEntity.ok("Saved");
    }
    @DeleteMapping("/delete")
    public ResponseEntity deleteNumber() {
        numberService.deleteNumber();
        return ResponseEntity.ok("Deleted");
    }
    @PutMapping("/update")
    public ResponseEntity updateNumber(@RequestBody @Valid NumberRequestDTO data) {
        numberService.updateNumber(data);
        return ResponseEntity.ok("Updated");
    }
    @GetMapping("/registered")
    public ResponseEntity getUserNumber() {
        NumberResponseDTO numberResponseDTO = numberService.getUserNumber();
        return ResponseEntity.ok(numberResponseDTO);
    }
}
