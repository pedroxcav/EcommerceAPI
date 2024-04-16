package com.ecommerce.api.model.dto.purchase;

import com.ecommerce.api.model.Address;
import com.ecommerce.api.model.Order;

import java.util.Set;

public record PurchaseDTO(Long id, Double totalPrice, Set<Order> orders, Address address) {
}
