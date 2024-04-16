package com.ecommerce.api.model.dto.order;

import com.ecommerce.api.model.Product;

public record OrderDTO(Long id, Integer amount, Double price, Product product) {
}
