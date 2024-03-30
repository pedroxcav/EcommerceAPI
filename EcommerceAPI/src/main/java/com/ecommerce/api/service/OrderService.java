package com.ecommerce.api.service;

import com.ecommerce.api.model.Order;
import com.ecommerce.api.model.Product;
import com.ecommerce.api.model.User;
import com.ecommerce.api.model.dto.order.OrderRequestDTO;
import com.ecommerce.api.model.dto.order.OrderResponseDTO;
import com.ecommerce.api.repository.OrderRepository;
import com.ecommerce.api.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserService userService;

    public void addToCart(OrderRequestDTO data) {
        var user = new User(userService.getAuthUser());
        Optional<Product> optionalProduct = productRepository.findById(data.productId());
        if(optionalProduct.isPresent() && optionalProduct.get().isActive()) {
            var product = optionalProduct.get();
            orderRepository.save(new Order(
                    data.amount(),
                    product.getPrice() * data.amount(),
                    product,
                    user
            ));
        } else
            throw new RuntimeException("The product doesn't exist!");
    }
    public void deleteFromCart(Long id) {
        var user = new User(userService.getAuthUser());
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if(optionalOrder.isPresent() && user.getCart().contains(optionalOrder.get())) {
            var order = optionalOrder.get();
            orderRepository.delete(order);
        } else
            throw new RuntimeException("The order doesn't exist in your cart");
    }
    public Set<OrderResponseDTO> getUserCart() {
        var user = new User(userService.getAuthUser());
        return user.getCart().stream()
                .map(order -> new OrderResponseDTO(
                        order.getId(),
                        order.getProduct(),
                        order.getAmount(),
                        order.getPrice()
                )).collect(Collectors.toSet());
    }
}
