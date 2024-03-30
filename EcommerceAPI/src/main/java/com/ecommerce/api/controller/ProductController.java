package com.ecommerce.api.controller;

import com.ecommerce.api.model.dto.product.ProductRequestDTO;
import com.ecommerce.api.model.dto.product.ProductResponseDTO;
import com.ecommerce.api.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("/new")
    public ResponseEntity newProduct(@RequestBody @Valid ProductRequestDTO data) {
        productService.newProduct(data);
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
    @PostMapping("/favorite/{id}")
    public ResponseEntity favorite(@PathVariable @Valid Long id) {
        productService.favorite(id);
        return ResponseEntity.ok("Favorited");
    }
    @DeleteMapping("/unfavorite/{id}")
    public ResponseEntity unfavorite(@PathVariable @Valid Long id) {
        productService.unfavorite(id);
        return ResponseEntity.ok("Unfavorited");
    }
    @GetMapping("/wishlist")
    public ResponseEntity getUserWishlist() {
        Set<ProductResponseDTO> userWishlist = productService.getUserWishlist();
        return ResponseEntity.ok(userWishlist);
    }
}
