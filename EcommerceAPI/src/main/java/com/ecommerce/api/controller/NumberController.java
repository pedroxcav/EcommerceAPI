package com.ecommerce.api.controller;

import com.ecommerce.api.model.dto.number.NumberDTO;
import com.ecommerce.api.service.NumberService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/numbers")
public class NumberController {
    private final NumberService numberService;

    public NumberController(NumberService numberService) {
        this.numberService = numberService;
    }

    @PostMapping
    public ResponseEntity<String> newNumber(@RequestBody @Valid NumberDTO data) {
        numberService.newNumber(data);
        return ResponseEntity.ok("Saved");
    }
    @DeleteMapping
    public ResponseEntity<String> deleteNumber() {
        numberService.deleteNumber();
        return ResponseEntity.ok("Deleted");
    }
    @PutMapping
    public ResponseEntity<String> updateNumber(@RequestBody @Valid NumberDTO data) {
        numberService.updateNumber(data);
        return ResponseEntity.ok("Updated");
    }
    @GetMapping("/me")
    public ResponseEntity<NumberDTO> getUserNumber() {
        var numberDTO = numberService.getUserNumber();
        return ResponseEntity.ok(numberDTO);
    }
}
