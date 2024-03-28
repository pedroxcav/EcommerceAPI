package com.ecommerce.api.service;

import com.ecommerce.api.model.Product;
import com.ecommerce.api.model.dto.ProductRequestDTO;
import com.ecommerce.api.model.dto.ProductResponseDTO;
import com.ecommerce.api.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public void register(ProductRequestDTO data) {
        var product = new Product(data.name(), data.description(), data.price());
        productRepository.save(product);
    }
    public List<ProductResponseDTO> getAllProducts() {
        List<Product> productList = productRepository.findAll();
        return productList
                .stream()
                .map(product -> new ProductResponseDTO(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice()))
                .collect(Collectors.toList());
    }
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
