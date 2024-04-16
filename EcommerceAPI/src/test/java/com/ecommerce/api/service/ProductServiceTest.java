package com.ecommerce.api.service;

import com.ecommerce.api.exception.InvalidPriceException;
import com.ecommerce.api.exception.NullProductException;
import com.ecommerce.api.exception.WishlistProductException;
import com.ecommerce.api.model.*;
import com.ecommerce.api.model.dto.product.ProductRequestDTO;
import com.ecommerce.api.model.dto.product.ProductDTO;
import com.ecommerce.api.model.dto.product.ProductUpdateDTO;
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

class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private AuthnService authnService;
    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Register Successfully")
    void newProduct_successful() {
        var data = new ProductRequestDTO("Iphone", "Apple smartphone.", 5000D);

        Assertions.assertDoesNotThrow(() -> productService.newProduct(data));

        verify(productRepository, times(1)).save(any(Product.class));
    }
    @Test
    @DisplayName("Register Unsuccessfully")
    void newProduct_unsuccessful() {
        var data = new ProductRequestDTO("Iphone", "Apple smartphone.", -5000D);

        Assertions.assertThrows(InvalidPriceException.class, () -> productService.newProduct(data));

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Delete Successfully")
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
    @DisplayName("Delete Unsuccessfully - NonExistent")
    void deleteProduct_unsuccessful() {
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        Assertions.assertThrows(NullProductException.class, () -> productService.deleteProduct(any(Long.class)));

        verify(productRepository, times(1)).findById(any(Long.class));
    }

    @Test
    @DisplayName("Get ALl Products Successfully")
    void getAllProducts() {
        var firstProduct = new Product("Iphone", "Apple smartphone.", 5000D);
        var secondProduct = new Product("Iphone", "Apple smartphone.", 5000D);
        when(productRepository.findAll()).thenReturn(List.of(firstProduct, secondProduct));

        List<ProductDTO> productDTOList = productService.getAllProducts();

        Assertions.assertEquals(2, productDTOList.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Favorite Successfully")
    void favorite_successful() {
        var product = new Product("Iphone", "Apple smartphone.", 5000D);
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

        Assertions.assertDoesNotThrow(() -> productService.favorite(product.getId()));

        verify(productRepository, times(1)).save(product);
    }
    @Test
    @DisplayName("Product Already Favorited")
    void favorite_unsuccessful_case01() {
        var product = new Product("Iphone", "Apple smartphone.", 5000D);
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
        user.getWishlist().add(product);
        when(authnService.getAuthnUser()).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        Assertions.assertThrows(WishlistProductException.class, () -> productService.favorite(product.getId()));
        verify(productRepository, never()).save(product);
    }
    @Test
    @DisplayName("Favorite Unsuccessfully - NonExistent")
    void favorite_unsuccessful_case02() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        when(authnService.getAuthnUser()).thenReturn(Optional.of(mock(User.class)));

        Assertions.assertThrows(NullProductException.class, () -> productService.favorite(1L));

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Unfavorite Successfully")
    void unfavorite_successful() {
        var product = new Product("Iphone", "Apple smartphone.", 5000D);
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
        user.getWishlist().add(product);
        when(authnService.getAuthnUser()).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        productService.unfavorite(product.getId());

        verify(productRepository, times(1)).save(product);
    }
    @Test
    @DisplayName("Product Doesn't Favorited")
    void unfavorite_unsuccessful_case01() {
        var product = new Product("Iphone", "Apple smartphone.", 5000D);
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

        Assertions.assertThrows(WishlistProductException.class, () -> productService.unfavorite(product.getId()));

        verify(productRepository, never()).save(product);
    }
    @Test
    @DisplayName("Unfavorite Unsuccessfully - NonExistent")
    void unfavorite_unsuccessful_case02() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        when(authnService.getAuthnUser()).thenReturn(Optional.of(mock(User.class)));

        Assertions.assertThrows(NullProductException.class, () -> productService.unfavorite(1L));

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Get User Wishlist Successfully")
    void getUserWishlist() {
        var firstProduct = new Product("Iphone", "Apple smartphone.", 5000D);
        var secondProduct = new Product("Iphone", "Apple smartphone.", 5000D);
        firstProduct.setId(1L);
        secondProduct.setId(2L);
        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(new ArrayList<>());
        user.setWishlist(Set.of(firstProduct, secondProduct));
        user.setPurchases(new ArrayList<>());
        when(authnService.getAuthnUser()).thenReturn(Optional.of(user));

        Set<ProductDTO> productDTOSet = productService.getUserWishlist();

        Assertions.assertEquals(user.getWishlist().size(), productDTOSet.size());
        verify(authnService, times(1)).getAuthnUser();
    }

    @Test
    @DisplayName("Update Successfully")
    void updateProduct_successful() {
        Product product = new Product("Iphone", "Apple smartphone.", 5000D);
        product.setOrders(new HashSet<>());
        product.setUsers(new HashSet<>());
        product.setId(1L);
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));

        var data = new ProductUpdateDTO(product.getId(), "New Description", 2500D);
        Assertions.assertDoesNotThrow(() -> productService.updateProduct(data));
        verify(productRepository, times(1)).findById(any(Long.class));
        verify(productRepository, times(1)).save(any(Product.class));
    }
    @Test
    @DisplayName("Update Unsuccessfully - NonExistent")
    void updateProduct_unsuccessful_case01() {
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        var data = new ProductUpdateDTO(1L, "New Description", 2500D);
        Assertions.assertThrows(NullProductException.class, () -> productService.updateProduct(data));
        verify(productRepository, times(1)).findById(any(Long.class));
        verify(productRepository, never()).save(any(Product.class));
    }
    @Test
    @DisplayName("Update Unsuccessfully - Invalid Price")
    void updateProduct_unsuccessful_case02() {
        Product product = new Product("Iphone", "Apple smartphone.", 5000D);
        product.setOrders(new HashSet<>());
        product.setUsers(new HashSet<>());
        product.setId(1L);
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));

        var data = new ProductUpdateDTO(product.getId(), "New Description", -2500D);
        Assertions.assertThrows(InvalidPriceException.class, () -> productService.updateProduct(data));
        verify(productRepository, never()).findById(any(Long.class));
        verify(productRepository, never()).save(any(Product.class));
    }
}