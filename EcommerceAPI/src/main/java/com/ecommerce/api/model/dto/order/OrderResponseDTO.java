package com.ecommerce.api.model.dto.order;

import com.ecommerce.api.model.Product;

public record OrderResponseDTO(Long id, Product product, Integer amount, Double price) {
}
