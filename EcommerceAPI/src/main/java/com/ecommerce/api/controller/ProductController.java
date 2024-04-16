package com.ecommerce.api.controller;

import com.ecommerce.api.model.dto.product.ProductRequestDTO;
import com.ecommerce.api.model.dto.product.ProductDTO;
import com.ecommerce.api.model.dto.product.ProductUpdateDTO;
import com.ecommerce.api.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<String> newProduct(@RequestBody @Valid ProductRequestDTO data) {
        productService.newProduct(data);
        return ResponseEntity.ok("Registered");
    }
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        var productResponseDTOList = productService.getAllProducts();
        return ResponseEntity.ok(productResponseDTOList);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable(name = "id") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Deleted");
    }
    @PostMapping("/wishlist/{id}")
    public ResponseEntity<String> favorite(@PathVariable @Valid Long id) {
        productService.favorite(id);
        return ResponseEntity.ok("Favorited");
    }
    @DeleteMapping("/wishlist/{id}")
    public ResponseEntity<String> unfavorite(@PathVariable @Valid Long id) {
        productService.unfavorite(id);
        return ResponseEntity.ok("Unfavorited");
    }
    @GetMapping("/me")
    public ResponseEntity<Set<ProductDTO>> getUserWishlist() {
        var userWishlist = productService.getUserWishlist();
        return ResponseEntity.ok(userWishlist);
    }
    @PutMapping
    public ResponseEntity<String> updateProduct(@RequestBody @Valid ProductUpdateDTO data) {
        productService.updateProduct(data);
        return ResponseEntity.ok("Updated");
    }
}
