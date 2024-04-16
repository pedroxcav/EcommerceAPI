package com.ecommerce.api.model.dto.order;

public record OrderUpdateDTO(Long id, Integer amount, Long productId) {
}
