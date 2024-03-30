package com.ecommerce.api.repository;

import com.ecommerce.api.model.Order;
import com.ecommerce.api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    void deleteByProduct(Product product);
}
