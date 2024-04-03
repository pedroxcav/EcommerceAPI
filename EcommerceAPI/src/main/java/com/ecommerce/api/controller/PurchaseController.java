package com.ecommerce.api.controller;

import com.ecommerce.api.model.dto.purchase.PurchaseRequestDTO;
import com.ecommerce.api.model.dto.purchase.PurchaseResponseDTO;
import com.ecommerce.api.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    @PostMapping("/new")
    public ResponseEntity buyOrders(@RequestBody @Valid PurchaseRequestDTO data) {
        purchaseService.buyOrders(data);
        return ResponseEntity.ok("Bought");
    }
    @GetMapping("/user")
    public ResponseEntity getUserPurchases() {
        Set<PurchaseResponseDTO> userPurchases = purchaseService.getUserPurchases();
        return ResponseEntity.ok(userPurchases);
    }
    @GetMapping("/registered")
    public ResponseEntity getAllPurchases() {
        Set<PurchaseResponseDTO> purchaseSet = purchaseService.getAllPurchases();
        return ResponseEntity.ok(purchaseSet);
    }
}
