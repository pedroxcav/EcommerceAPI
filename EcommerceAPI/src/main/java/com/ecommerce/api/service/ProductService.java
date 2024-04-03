package com.ecommerce.api.service;

import com.ecommerce.api.model.Order;
import com.ecommerce.api.model.Product;
import com.ecommerce.api.model.User;
import com.ecommerce.api.model.dto.product.ProductRequestDTO;
import com.ecommerce.api.model.dto.product.ProductResponseDTO;
import com.ecommerce.api.repository.OrderRepository;
import com.ecommerce.api.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserService userService;

    public ProductService(ProductRepository productRepository, OrderRepository orderRepository, UserService userService) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userService = userService;
    }

    public void newProduct(ProductRequestDTO data) {
        var product = new Product(data.name(), data.description(), data.price());
        productRepository.save(product);
    }
    public void deleteProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if(optionalProduct.isPresent() && optionalProduct.get().isActive()) {
            Product product = optionalProduct.get();
            if(product.getOrders().stream().anyMatch(Order::isCompleted)) {
                product.getOrders().stream()
                        .filter(order -> !order.isCompleted())
                        .forEach(order -> {
                            orderRepository.delete(order);
                            product.getOrders().remove(order);
                        });
                product.setActive(false);
                product.setUsers(null);
                productRepository.save(product);
            } else {
                orderRepository.deleteAll(product.getOrders());
                productRepository.delete(product);
            }
        } else
            throw new RuntimeException("The product doesn't exists!");
    }
    public List<ProductResponseDTO> getAllProducts() {
        List<Product> productList = productRepository.findAll();
        return productList
                .stream()
                .filter(Product::isActive)
                .map(product -> new ProductResponseDTO(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice()))
                .collect(Collectors.toList());
    }
    public void favorite(Long id) {
        var user = new User(userService.getAuthUser());
        Optional<Product> optionalProduct = productRepository.findById(id);
        if(optionalProduct.isPresent() && optionalProduct.get().isActive()) {
            var product = optionalProduct.get();
            Set<Product> wishlist = user.getWishlist();
            Set<User> userList = product.getUsers();
            if(!wishlist.contains(product)) {
                wishlist.add(product);
                userList.add(user);
                productRepository.save(product);
            } else
                throw new RuntimeException("The product was already favorited!");
        } else
            throw new RuntimeException("The product doesn't exist!");
    }
    public void unfavorite(Long id) {
        var user = new User(userService.getAuthUser());
        Optional<Product> optionalProduct = productRepository.findById(id);
        if(optionalProduct.isPresent() && optionalProduct.get().isActive()) {
            var product = optionalProduct.get();
            Set<Product> wishlist = user.getWishlist();
            Set<User> userList = product.getUsers();
            if(wishlist.contains(product)) {
                wishlist.remove(product);
                userList.remove(user);
                productRepository.save(product);
            } else
                throw new RuntimeException("The product hasn't been favorited yet!");
        } else
            throw new RuntimeException("The product doesn't exist!");
    }
    public Set<ProductResponseDTO> getUserWishlist() {
        var user = new User(userService.getAuthUser());
        return user.getWishlist().stream()
                .map(product -> new ProductResponseDTO(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice()
                )).collect(Collectors.toSet());
    }
}
