package com.ecommerce.api.controller;

import com.ecommerce.api.model.dto.address.AddressRequestDTO;
import com.ecommerce.api.model.dto.address.AddressResponseDTO;
import com.ecommerce.api.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/address")
public class AddressController {
    @Autowired
    private AddressService addressService;

    @PostMapping("/new")
    public ResponseEntity<String> newAddress(@RequestBody @Valid AddressRequestDTO data) {
        addressService.newAddress(data);
        return ResponseEntity.ok("Saved");
    }
    @GetMapping("/registered")
    public ResponseEntity<Set<AddressResponseDTO>> getUserAdresses() {
        var addressResponseDTOList = addressService.getUserAdresses();
        return ResponseEntity.ok(addressResponseDTOList);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAddress(@PathVariable @Valid Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.ok("Deleted");
    }
}
