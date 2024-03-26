package com.ecommerce.api.controller;

import com.ecommerce.api.model.Product;
import com.ecommerce.api.model.dto.ProductRequest;
import com.ecommerce.api.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("/new")
    public ResponseEntity newProduct(@RequestBody @Valid ProductRequest data) {
        productService.newProduct(data);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/registered")
    public ResponseEntity getRegisteredProducts() {
        List<Product> registeredProducts = productService.findAll();
        return ResponseEntity.ok(registeredProducts);
    }
}
