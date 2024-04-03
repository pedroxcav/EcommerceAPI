package com.ecommerce.api.model.dto.order;

import com.ecommerce.api.model.Product;

public record OrderResponseDTO(Long id, Integer amount, Double price, Product product) {
}
