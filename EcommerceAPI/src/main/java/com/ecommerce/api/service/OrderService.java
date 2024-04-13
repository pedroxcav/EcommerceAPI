package com.ecommerce.api.service;

import com.ecommerce.api.exception.InvalidAmountException;
import com.ecommerce.api.exception.NullOrderException;
import com.ecommerce.api.exception.NullProductException;
import com.ecommerce.api.model.Order;
import com.ecommerce.api.model.Product;
import com.ecommerce.api.model.User;
import com.ecommerce.api.model.dto.order.OrderRequestDTO;
import com.ecommerce.api.model.dto.order.OrderResponseDTO;
import com.ecommerce.api.repository.OrderRepository;
import com.ecommerce.api.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, UserService userService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userService = userService;
    }

    public void addToCart(OrderRequestDTO data) {
        if(data.amount() < 1) throw new InvalidAmountException();
        Optional<User> optionalUser = userService.getAuthnUser();
        optionalUser.ifPresent(user -> {
            Optional<Product> optionalProduct = productRepository.findById(data.productId());
            if(optionalProduct.isPresent() && optionalProduct.get().isActive()) {
                Product product = optionalProduct.get();
                Optional<Order> optionalOrder = Optional.empty();
                for (Order order : product.getOrders())
                    if(order.isCompleted() && user.getActiveCart().contains(order))
                        optionalOrder = Optional.of(order);
                optionalOrder.ifPresentOrElse(
                        order -> {
                            int amount = order.getAmount() + data.amount();
                            order.setAmount(amount);
                            order.setPrice(amount * order.getProduct().getPrice());
                            orderRepository.save(order);
                        }, () -> orderRepository.save(
                                new Order(product, data.amount(), product.getPrice() * data.amount(), user)));
            } else
                throw new NullProductException();
        });
    }
    public void deleteFromCart(Long id) {
        Optional<User> optionalUser = userService.getAuthnUser();
        optionalUser.ifPresent(user -> {
            Optional<Order> optionalOrder = orderRepository.findById(id);
            if(optionalOrder.isPresent() && !optionalOrder.get().isCompleted() && user.getActiveCart().contains(optionalOrder.get())) {
                var order = optionalOrder.get();
                orderRepository.delete(order);
            } else
                throw new NullOrderException("The order doesn't exist in your cart");
        });
    }
    public Set<OrderResponseDTO> getUserCart() {
        Optional<User> optionalUser = userService.getAuthnUser();
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            return user.getActiveCart().stream()
                    .map(order -> new OrderResponseDTO(
                            order.getId(),
                            order.getAmount(),
                            order.getPrice(),
                            order.getProduct()
                    )).collect(Collectors.toSet());
        } else
            return null;
    }
}
