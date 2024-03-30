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
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/new")
    public ResponseEntity addToCart(@RequestBody @Valid OrderRequestDTO data) {
        orderService.addToCart(data);
        return ResponseEntity.ok("Added");
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteFromCart(@PathVariable("id") @Valid Long id) {
        orderService.deleteFromCart(id);
        return ResponseEntity.ok("Deleted");
    }
    @GetMapping("/cart")
    public ResponseEntity getUserCart() {
        Set<OrderResponseDTO> orderResponseDTOSet = orderService.getUserCart();
        return ResponseEntity.ok(orderResponseDTOSet);
    }
}
