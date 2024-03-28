package com.ecommerce.api.controller;

import com.ecommerce.api.model.dto.ProductRequestDTO;
import com.ecommerce.api.model.dto.ProductResponseDTO;
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

    @PostMapping("/register")
    public ResponseEntity registerProduct(@RequestBody @Valid ProductRequestDTO data) {
        productService.register(data);
        return ResponseEntity.ok("Registred");
    }
    @GetMapping("/registered")
    public ResponseEntity getAllProducts() {
        List<ProductResponseDTO> productResponseDTOList = productService.getAllProducts();
        return ResponseEntity.ok(productResponseDTOList);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteProduct(@PathVariable(name = "id") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Deleted");
    }
}
