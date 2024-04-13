package com.ecommerce.api.service;

import com.ecommerce.api.exception.NullAddressException;
import com.ecommerce.api.exception.NullOrderException;
import com.ecommerce.api.model.*;
import com.ecommerce.api.model.dto.purchase.PurchaseRequestDTO;
import com.ecommerce.api.model.dto.purchase.PurchaseResponseDTO;
import com.ecommerce.api.model.enums.Role;
import com.ecommerce.api.repository.AddressRepository;
import com.ecommerce.api.repository.OrderRepository;
import com.ecommerce.api.repository.PurchaseRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.mockito.Mockito.*;

class PurchaseServiceTest {
    @Mock
    private PurchaseRepository purchaseRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private PurchaseService purchaseService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Buy Order Successfully")
    void buyOrders_successful() {
        var address = new Address("08510000", "House Number",
                "Street Name", "Neighborhood Name",
                "City Name", "State Name", mock(User.class));
        address.setPurchases(new HashSet<>());

        var product = new Product("Iphone", "Apple Smartphone.",5000D);
        product.setOrders(new HashSet<>());
        product.setUsers(new HashSet<>());
        product.setId(1L);

        var order = new Order(product, 2, 10000D, mock(User.class));
        order.setId(1L);

        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        user.setAdresses(Set.of(address));
        user.setCart(List.of(order));
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        when(userService.getAuthnUser()).thenReturn(Optional.of(user));
        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        var data = new PurchaseRequestDTO(Set.of(1L), address.getId());

        Assertions.assertDoesNotThrow(() -> purchaseService.buyOrders(data));
        verify(purchaseRepository, times(1)).save(any(Purchase.class));
        verify(addressRepository, times(1)).findById(data.addressId());
        verify(orderRepository, times(1)).findById(order.getId());
        verify(userService, times(1)).getAuthnUser();
    }
    @Test
    @DisplayName("Unsuccessfully - Address NonExistent")
    void buyOrders_unsuccessful_case01() {
        var address = new Address("08510000", "House Number",
                "Street Name", "Neighborhood Name",
                "City Name", "State Name", mock(User.class));
        address.setPurchases(new HashSet<>());

        var data = new PurchaseRequestDTO(Set.of(1L), address.getId());
        when(userService.getAuthnUser()).thenReturn(Optional.of(mock(User.class)));
        when(addressRepository.findById(data.addressId())).thenReturn(Optional.empty());

        Assertions.assertThrows(NullAddressException.class, () -> purchaseService.buyOrders(data));
        verify(addressRepository, times(1)).findById(data.addressId());
        verify(userService, times(1)).getAuthnUser();
        verify(purchaseRepository, never()).save(any(Purchase.class));
        verify(orderRepository, never()).findById(any());
    }
    @Test
    @DisplayName("Unsuccessfully - Order NonExistent")
    void buyOrders_unsuccessful_case02() {
        var address = new Address("08510000", "House Number",
                "Street Name", "Neighborhood Name",
                "City Name", "State Name", mock(User.class));
        address.setPurchases(new HashSet<>());

        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        user.setAdresses(Set.of(address));
        user.setCart(new ArrayList<>());
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        when(userService.getAuthnUser()).thenReturn(Optional.of(user));
        var data = new PurchaseRequestDTO(Set.of(1L), address.getId());
        when(addressRepository.findById(data.addressId())).thenReturn(Optional.of(address));
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(NullOrderException.class, () -> purchaseService.buyOrders(data));
        verify(addressRepository, times(1)).findById(data.addressId());
        verify(orderRepository, times(1)).findById(any());
        verify(userService, times(1)).getAuthnUser();
        verify(purchaseRepository, never()).save(any(Purchase.class));
    }

    @Test
    @DisplayName("Get User Purchases Successfully")
    void getUserPurchases_successful() {
        var firstPurchase = new Purchase(
                Set.of(mock(Order.class), mock(Order.class)),
                10000D, mock(Address.class), mock(User.class));
        firstPurchase.setId(1L);
        var secondPurchase = new Purchase(
                Set.of(mock(Order.class), mock(Order.class)),
                10000D, mock(Address.class), mock(User.class));
        secondPurchase.setId(2L);

        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(new ArrayList<>());
        user.setWishlist(new HashSet<>());
        user.setPurchases(List.of(firstPurchase, secondPurchase));
        when(userService.getAuthnUser()).thenReturn(Optional.of(user));

        Set<PurchaseResponseDTO> purchaseResponseDTOSet = purchaseService.getUserPurchases();
        Assertions.assertEquals(user.getPurchases().size(), purchaseResponseDTOSet.size());
        verify(userService, times(1)).getAuthnUser();
    }

    @Test
    @DisplayName("Get All Purchases Successfully")
    void getAllPurchases_successful() {
        var firstPurchase = new Purchase(
                Set.of(mock(Order.class), mock(Order.class)),
                10000D, mock(Address.class), mock(User.class));
        firstPurchase.setId(1L);
        var secondPurchase = new Purchase(
                Set.of(mock(Order.class), mock(Order.class)),
                10000D, mock(Address.class), mock(User.class));
        secondPurchase.setId(2L);
        List<Purchase> purchases = List.of(firstPurchase, secondPurchase);
        when(purchaseRepository.findAll()).thenReturn(purchases);

        Set<PurchaseResponseDTO> purchaseResponseDTOSet = purchaseService.getAllPurchases();

        Assertions.assertEquals(purchases.size(), purchaseResponseDTOSet.size());
        verify(purchaseRepository, times(1)).findAll();
    }
}