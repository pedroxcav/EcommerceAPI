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
@RequestMapping("/purchases")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    @PostMapping
    public ResponseEntity<String> buyOrders(@RequestBody @Valid PurchaseRequestDTO data) {
        purchaseService.buyOrders(data);
        return ResponseEntity.ok("Bought");
    }
    @GetMapping("/me")
    public ResponseEntity<Set<PurchaseResponseDTO>> getUserPurchases() {
        var userPurchases = purchaseService.getUserPurchases();
        return ResponseEntity.ok(userPurchases);
    }
    @GetMapping
    public ResponseEntity<Set<PurchaseResponseDTO>> getAllPurchases() {
        var purchaseSet = purchaseService.getAllPurchases();
        return ResponseEntity.ok(purchaseSet);
    }
}
