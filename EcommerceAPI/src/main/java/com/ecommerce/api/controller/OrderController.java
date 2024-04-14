package com.ecommerce.api.controller;

import com.ecommerce.api.model.dto.order.OrderRequestDTO;
import com.ecommerce.api.model.dto.order.OrderResponseDTO;
import com.ecommerce.api.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<String> addToCart(@RequestBody @Valid OrderRequestDTO data) {
        orderService.addToCart(data);
        return ResponseEntity.ok("Added");
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFromCart(@PathVariable("id") @Valid Long id) {
        orderService.deleteFromCart(id);
        return ResponseEntity.ok("Deleted");
    }
    @GetMapping("/me")
    public ResponseEntity<Set<OrderResponseDTO>> getUserCart() {
        var orderResponseDTOSet = orderService.getUserCart();
        return ResponseEntity.ok(orderResponseDTOSet);
    }
}
