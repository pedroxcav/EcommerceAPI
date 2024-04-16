package com.ecommerce.api.service;

import com.ecommerce.api.exception.InvalidAmountException;
import com.ecommerce.api.exception.NullOrderException;
import com.ecommerce.api.exception.NullProductException;
import com.ecommerce.api.model.*;
import com.ecommerce.api.model.dto.order.OrderRequestDTO;
import com.ecommerce.api.model.dto.order.OrderDTO;
import com.ecommerce.api.model.dto.order.OrderUpdateDTO;
import com.ecommerce.api.model.enums.Role;
import com.ecommerce.api.repository.OrderRepository;
import com.ecommerce.api.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.mockito.Mockito.*;

class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private AuthnService authnService;
    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Add Successfully")
    void addToCart_successful() {
        var product = new Product("Iphone", "Apple Smartphone.",5000D);
        product.setOrders(new HashSet<>());
        product.setUsers(new HashSet<>());
        product.setId(1L);

        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(new ArrayList<>());
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        when(authnService.getAuthnUser()).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        OrderRequestDTO data = new OrderRequestDTO(2, product.getId());
        Assertions.assertDoesNotThrow(() -> orderService.addToCart(data));

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(productRepository, times(1)).findById(product.getId());
        verify(authnService, times(1)).getAuthnUser();
    }
    @Test
    @DisplayName("Add Unsuccessfully - Invalid Amount")
    void addToCart_unsuccessful_case01() {
        var product = new Product("Iphone", "Apple Smartphone.",5000D);
        product.setOrders(new HashSet<>());
        product.setUsers(new HashSet<>());
        product.setId(1L);

        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(new ArrayList<>());
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        when(authnService.getAuthnUser()).thenReturn(Optional.of(user));

        OrderRequestDTO data = new OrderRequestDTO(-2, product.getId());
        Assertions.assertThrows(InvalidAmountException.class, () -> orderService.addToCart(data));

        verify(orderRepository, never()).save(any(Order.class));
        verify(productRepository, never()).findById(product.getId());
        verify(authnService, never()).getAuthnUser();
    }
    @Test
    @DisplayName("Add Unsuccessfully - NonExistent Product")
    void addToCart_unsuccessful_case02() {
        var product = new Product("Iphone", "Apple Smartphone.",5000D);
        product.setOrders(new HashSet<>());
        product.setUsers(new HashSet<>());
        product.setId(1L);

        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());
        when(authnService.getAuthnUser()).thenReturn(Optional.of(mock(User.class)));

        OrderRequestDTO data = new OrderRequestDTO(2, product.getId());
        Assertions.assertThrows(NullProductException.class, () -> orderService.addToCart(data));

        verify(orderRepository, never()).save(any(Order.class));
        verify(productRepository, times(1)).findById(product.getId());
        verify(authnService, times(1)).getAuthnUser();
    }

    @Test
    @DisplayName("Delete Successfully")
    void deleteFromCart_successful() {
        var product = new Product("Iphone", "Apple Smartphone.",5000D);
        product.setOrders(new HashSet<>());
        product.setUsers(new HashSet<>());
        product.setId(1L);

        var order = new Order(product, 2, 10000D, mock(User.class));
        order.setId(1L);

        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(List.of(order));
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        when(authnService.getAuthnUser()).thenReturn(Optional.of(user));
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        Assertions.assertDoesNotThrow(() -> orderService.deleteFromCart(order.getId()));
        verify(orderRepository, times(1)).delete(any(Order.class));
        verify(authnService, times(1)).getAuthnUser();
    }
    @Test
    @DisplayName("Delete Unsuccessfully - NonExistent Order")
    void deleteFromCart_unsuccessful() {
        var product = new Product("Iphone", "Apple Smartphone.",5000D);
        product.setOrders(new HashSet<>());
        product.setUsers(new HashSet<>());
        product.setId(1L);

        var order = new Order(product, 2, 10000D, mock(User.class));
        order.setId(1L);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(authnService.getAuthnUser()).thenReturn(Optional.of(mock(User.class)));

        Assertions.assertThrows(NullOrderException.class, () -> orderService.deleteFromCart(order.getId()));
        verify(orderRepository, never()).delete(any(Order.class));
        verify(authnService, times(1)).getAuthnUser();
    }

    @Test
    @DisplayName("Get User Cart Successfully")
    void getUserCart() {
        var product = new Product("Iphone", "Apple Smartphone.",5000D);
        product.setOrders(new HashSet<>());
        product.setUsers(new HashSet<>());
        product.setId(1L);

        var firstOrder = new Order(product, 2, product.getPrice() * 2, mock(User.class));
        var secondOrder = new Order(product, 2, product.getPrice() * 2, mock(User.class));
        firstOrder.setId(1L);
        secondOrder.setId(2L);

        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(List.of(firstOrder, secondOrder));
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        when(authnService.getAuthnUser()).thenReturn(Optional.of(user));

        Set<OrderDTO> orderDTOSet = orderService.getUserCart();
        Assertions.assertEquals(user.getCart().size(), orderDTOSet.size());
        verify(authnService, times(1)).getAuthnUser();
    }

    @Test
    @DisplayName("Updates Successfully")
    void updateOrder_successful() {
        var firstProduct = new Product("Iphone", "Apple Smartphone.",5000D);
        firstProduct.setOrders(new HashSet<>());
        firstProduct.setUsers(new HashSet<>());
        firstProduct.setId(1L);
        var secondProduct = new Product("Iphone", "Apple Smartphone.",2500D);
        secondProduct.setOrders(new HashSet<>());
        secondProduct.setUsers(new HashSet<>());
        secondProduct.setId(2L);
        var order = new Order(firstProduct, 2, 10000D, mock(User.class));
        order.setId(1L);
        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(List.of(order));
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        when(authnService.getAuthnUser()).thenReturn(Optional.of(user));
        when(productRepository.findById(secondProduct.getId())).thenReturn(Optional.of(secondProduct));
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        var data = new OrderUpdateDTO(order.getId(), 3, secondProduct.getId());
        Assertions.assertDoesNotThrow(() -> orderService.updateOrder(data));

        verify(authnService, times(1)).getAuthnUser();
        verify(orderRepository, times(1)).save(any(Order.class));
    }
    @Test
    @DisplayName("Updates Unsuccessfully - NonExistent Product")
    void updateOrder_unsuccessful_case01() {
        var firstProduct = new Product("Iphone", "Apple Smartphone.",5000D);
        firstProduct.setOrders(new HashSet<>());
        firstProduct.setUsers(new HashSet<>());
        firstProduct.setId(1L);
        var order = new Order(firstProduct, 2, 10000D, mock(User.class));
        order.setId(1L);
        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(List.of(order));
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        when(authnService.getAuthnUser()).thenReturn(Optional.of(user));
        when(productRepository.findById(any())).thenReturn(Optional.empty());
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        var data = new OrderUpdateDTO(order.getId(), 3, 2L);
        Assertions.assertThrows(NullProductException.class, () -> orderService.updateOrder(data));

        verify(authnService, times(1)).getAuthnUser();
        verify(orderRepository, never()).save(any(Order.class));
    }
    @Test
    @DisplayName("Updates Unsuccessfully - NonExistent Order")
    void updateOrder_unsuccessful_case02() {
        var secondProduct = new Product("Iphone", "Apple Smartphone.",2500D);
        secondProduct.setOrders(new HashSet<>());
        secondProduct.setUsers(new HashSet<>());
        secondProduct.setId(2L);
        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(new ArrayList<>());
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        when(authnService.getAuthnUser()).thenReturn(Optional.of(user));
        when(productRepository.findById(secondProduct.getId())).thenReturn(Optional.of(secondProduct));
        when(orderRepository.findById(any())).thenReturn(Optional.empty());

        var data = new OrderUpdateDTO(1L, 3, secondProduct.getId());
        Assertions.assertThrows(NullOrderException.class, () -> orderService.updateOrder(data));

        verify(authnService, times(1)).getAuthnUser();
        verify(orderRepository, never()).save(any(Order.class));
    }
    @Test
    @DisplayName("Updates Unsuccessfully - Invalid Amount")
    void updateOrder_unsuccessful_case03() {
        var firstProduct = new Product("Iphone", "Apple Smartphone.",5000D);
        firstProduct.setOrders(new HashSet<>());
        firstProduct.setUsers(new HashSet<>());
        firstProduct.setId(1L);
        var secondProduct = new Product("Iphone", "Apple Smartphone.",2500D);
        secondProduct.setOrders(new HashSet<>());
        secondProduct.setUsers(new HashSet<>());
        secondProduct.setId(2L);
        var order = new Order(firstProduct, 2, 10000D, mock(User.class));
        order.setId(1L);
        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(List.of(order));
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        when(authnService.getAuthnUser()).thenReturn(Optional.of(user));
        when(productRepository.findById(secondProduct.getId())).thenReturn(Optional.of(secondProduct));
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        var data = new OrderUpdateDTO(order.getId(), -3, secondProduct.getId());
        Assertions.assertThrows(InvalidAmountException.class, () -> orderService.updateOrder(data));

        verify(authnService, times(1)).getAuthnUser();
        verify(orderRepository, never()).save(any(Order.class));
    }
}