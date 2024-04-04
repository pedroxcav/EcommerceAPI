package com.ecommerce.api.controller;

import com.ecommerce.api.model.dto.number.NumberDTO;
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
    public ResponseEntity<String> newNumber(@RequestBody @Valid NumberDTO data) {
        numberService.newNumber(data);
        return ResponseEntity.ok("Saved");
    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteNumber() {
        numberService.deleteNumber();
        return ResponseEntity.ok("Deleted");
    }
    @PutMapping("/update")
    public ResponseEntity<String> updateNumber(@RequestBody @Valid NumberDTO data) {
        numberService.updateNumber(data);
        return ResponseEntity.ok("Updated");
    }
    @GetMapping("/registered")
    public ResponseEntity<NumberDTO> getUserNumber() {
        var numberDTO = numberService.getUserNumber();
        return ResponseEntity.ok(numberDTO);
    }
}
