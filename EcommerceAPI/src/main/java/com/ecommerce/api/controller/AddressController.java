package com.ecommerce.api.controller;

import com.ecommerce.api.model.dto.AddressRequestDTO;
import com.ecommerce.api.model.dto.AddressResponseDTO;
import com.ecommerce.api.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {
    @Autowired
    private AddressService addressService;

    @PostMapping("/new")
    public ResponseEntity newAddress(@RequestBody @Valid AddressRequestDTO data) {
        addressService.newAdress(data);
        return ResponseEntity.ok("Saved");
    }
    @GetMapping("/registered")
    public ResponseEntity getUserAdresses() {
        List<AddressResponseDTO> addressResponseDTOList = addressService.getUserAdresses();
        return ResponseEntity.ok(addressResponseDTOList);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteAddress(@PathVariable @Valid Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.ok("Deleted");
    }
}
