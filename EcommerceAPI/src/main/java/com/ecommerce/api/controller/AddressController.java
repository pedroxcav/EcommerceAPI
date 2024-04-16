package com.ecommerce.api.controller;

import com.ecommerce.api.model.dto.address.AddressRequestDTO;
import com.ecommerce.api.model.dto.address.AddressDTO;
import com.ecommerce.api.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/adresses")
public class AddressController {
    @Autowired
    private AddressService addressService;

    @PostMapping
    public ResponseEntity<String> newAddress(@RequestBody @Valid AddressRequestDTO data) {
        addressService.newAddress(data);
        return ResponseEntity.ok("Saved");
    }
    @GetMapping("/me")
    public ResponseEntity<Set<AddressDTO>> getUserAdresses() {
        var addressResponseDTOList = addressService.getUserAdresses();
        return ResponseEntity.ok(addressResponseDTOList);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAddress(@PathVariable @Valid Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.ok("Deleted");
    }
    @PutMapping
    public ResponseEntity<String> updateAddress(@RequestBody @Valid AddressDTO data) {
        addressService.updateAddress(data);
        return ResponseEntity.ok("Updated");
    }
}
