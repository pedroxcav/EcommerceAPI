package com.ecommerce.api.service;

import com.ecommerce.api.exception.NullProductException;
import com.ecommerce.api.exception.WishlistProductException;
import com.ecommerce.api.model.*;
import com.ecommerce.api.model.Number;
import com.ecommerce.api.model.dto.product.ProductRequestDTO;
import com.ecommerce.api.model.dto.product.ProductResponseDTO;
import com.ecommerce.api.model.dto.user.UserResponseDTO;
import com.ecommerce.api.model.enums.Role;
import com.ecommerce.api.repository.OrderRepository;
import com.ecommerce.api.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.mockito.Mockito.*;

class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void newProduct() {
        var data = new ProductRequestDTO("Iphone", "Apple smartphone.", 5000D);

        productService.newProduct(data);

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void deleteProduct_successful() {
        Product product = new Product("Iphone", "Apple smartphone.", 5000D);
        product.setOrders(new HashSet<>());
        product.setUsers(new HashSet<>());
        product.setId(1L);
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));

        Assertions.assertDoesNotThrow(() -> productService.deleteProduct(any(Long.class)));

        verify(productRepository, times(1)).findById(any(Long.class));
        verify(orderRepository, times(1)).deleteAll(any());
        verify(productRepository, times(1)).delete(product);
    }
    @Test
    void deleteProduct_unsuccessful() {
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        Assertions.assertThrows(NullProductException.class, () -> productService.deleteProduct(any(Long.class)));

        verify(productRepository, times(1)).findById(any(Long.class));
    }

    @Test
    void getAllProducts() {
        var firstProduct = new Product("Iphone", "Apple smartphone.", 5000D);
        var secondProduct = new Product("Iphone", "Apple smartphone.", 5000D);
        when(productRepository.findAll()).thenReturn(List.of(firstProduct, secondProduct));

        List<ProductResponseDTO> productResponseDTOList = productService.getAllProducts();

        Assertions.assertEquals(2, productResponseDTOList.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void favorite_successful() {
        var product = new Product("Iphone", "Apple smartphone.", 5000D);
        product.setOrders(new HashSet<>());
        product.setUsers(new HashSet<>());
        product.setId(1L);
        Set<Address> adresses = new HashSet<>();
        List<Order> cart = new ArrayList<>();
        Set<Product> wishlist = new HashSet<>();
        List<Purchase> purchases = new ArrayList<>();
        var number = new Number("11", "910000000", mock(User.class));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(userService.getAuthUser()).thenReturn(
                new UserResponseDTO(null,"Pedro Cavalcanti", "pedroxcav",
                        "01010101010","pedroxcav@icloud.com","1234",
                        Role.ADMIN, number, adresses, cart, wishlist, purchases));

        Assertions.assertDoesNotThrow(() -> productService.favorite(product.getId()));

        verify(productRepository, times(1)).save(product);
    }
    @Test
    void favorite_unsuccessful_case01() {
        var product = new Product("Iphone", "Apple smartphone.", 5000D);
        product.setOrders(new HashSet<>());
        product.setUsers(new HashSet<>());
        product.setId(1L);
        Set<Address> adresses = new HashSet<>();
        List<Order> cart = new ArrayList<>();
        Set<Product> wishlist = new HashSet<>();
        List<Purchase> purchases = new ArrayList<>();
        wishlist.add(product);
        var number = new Number("11", "910000000", mock(User.class));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(userService.getAuthUser()).thenReturn(
                new UserResponseDTO(null,"Pedro Cavalcanti", "pedroxcav",
                        "01010101010","pedroxcav@icloud.com","1234",
                        Role.ADMIN, number, adresses, cart, wishlist, purchases));

        Assertions.assertThrows(WishlistProductException.class, () -> productService.favorite(product.getId()));
        verify(productRepository, never()).save(product);
    }
    @Test
    void favorite_unsuccessful_case02() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        when(userService.getAuthUser()).thenReturn(mock(UserResponseDTO.class));

        Assertions.assertThrows(NullProductException.class, () -> productService.favorite(1L));

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void unfavorite_successful() {
        var product = new Product("Iphone", "Apple smartphone.", 5000D);
        product.setOrders(new HashSet<>());
        product.setUsers(new HashSet<>());
        product.setId(1L);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        Set<Address> adresses = new HashSet<>();
        List<Order> cart = new ArrayList<>();
        Set<Product> wishlist = new HashSet<>();
        List<Purchase> purchases = new ArrayList<>();
        wishlist.add(product);
        var number = new Number("11", "910000000", mock(User.class));
        when(userService.getAuthUser()).thenReturn(
                new UserResponseDTO(null,"Pedro Cavalcanti", "pedroxcav",
                        "01010101010","pedroxcav@icloud.com","1234",
                        Role.ADMIN, number, adresses, cart, wishlist, purchases));

        productService.unfavorite(product.getId());

        verify(productRepository, times(1)).save(product);
    }
    @Test
    void unfavorite_unsuccessful_case01() {
        var product = new Product("Iphone", "Apple smartphone.", 5000D);
        product.setOrders(new HashSet<>());
        product.setUsers(new HashSet<>());
        product.setId(1L);
        Set<Address> adresses = new HashSet<>();
        List<Order> cart = new ArrayList<>();
        Set<Product> wishlist = new HashSet<>();
        List<Purchase> purchases = new ArrayList<>();
        var number = new Number("11", "910000000", mock(User.class));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(userService.getAuthUser()).thenReturn(
                new UserResponseDTO(null,"Pedro Cavalcanti", "pedroxcav",
                        "01010101010","pedroxcav@icloud.com","1234",
                        Role.ADMIN, number, adresses, cart, wishlist, purchases));

        Assertions.assertThrows(WishlistProductException.class, () -> productService.unfavorite(product.getId()));

        verify(productRepository, never()).save(product);
    }
    @Test
    void unfavorite_unsuccessful_case02() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        when(userService.getAuthUser()).thenReturn(mock(UserResponseDTO.class));

        Assertions.assertThrows(NullProductException.class, () -> productService.unfavorite(1L));

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getUserWishlist() {
        var firstProduct = new Product("Iphone", "Apple smartphone.", 5000D);
        var secondProduct = new Product("Iphone", "Apple smartphone.", 5000D);
        firstProduct.setId(1L);
        secondProduct.setId(2L);
        Set<Address> adresses = new HashSet<>();
        List<Order> cart = new ArrayList<>();
        Set<Product> wishlist = Set.of(firstProduct, secondProduct);
        List<Purchase> purchases = new ArrayList<>();
        var number = new Number("11", "910000000", mock(User.class));
        when(userService.getAuthUser()).thenReturn(
                new UserResponseDTO(null,"Pedro Cavalcanti", "pedroxcav",
                        "01010101010","pedroxcav@icloud.com","1234",
                        Role.ADMIN, number, adresses, cart, wishlist, purchases));

        Set<ProductResponseDTO> productResponseDTOSet = productService.getUserWishlist();

        Assertions.assertEquals(wishlist.size(), productResponseDTOSet.size());
        verify(userService, times(1)).getAuthUser();
    }
}