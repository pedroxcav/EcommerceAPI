package com.ecommerce.api.service;

import com.ecommerce.api.model.Product;
import com.ecommerce.api.model.dto.ProductRequest;
import com.ecommerce.api.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Product register(ProductRequest data) {
        return productRepository.save(new Product(data.name(), data.description(), data.price()));
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }
}
