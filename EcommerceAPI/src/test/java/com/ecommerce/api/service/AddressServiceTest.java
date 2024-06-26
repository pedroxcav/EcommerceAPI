package com.ecommerce.api.service;

import com.ecommerce.api.exception.NullAddressException;
import com.ecommerce.api.model.*;
import com.ecommerce.api.model.dto.address.AddressRequestDTO;
import com.ecommerce.api.model.dto.address.AddressDTO;
import com.ecommerce.api.model.enums.Role;
import com.ecommerce.api.repository.AddressRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.mockito.Mockito.*;

class AddressServiceTest {
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private AuthnService authnService;
    @InjectMocks
    private AddressService addressService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Register Address Successfully")
    void newAddress() {
        var data = new AddressRequestDTO("08510000", "House Number",
                "Street Name", "Neighborhood Name",
                "City Name", "State Name");
        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(new ArrayList<>());
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        when(authnService.getAuthnUser()).thenReturn(Optional.of(user));

        addressService.newAddress(data);

        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    @DisplayName("Get User Adresses Successfully")
    void getUserAdresses() {
        var firstAddress = new Address("08510000", "House Number",
                "Street Name", "Neighborhood Name",
                "City Name", "State Name", mock(User.class));
        firstAddress.setId(1L);
        var secondAddress = new Address("08510000", "House Number",
                "Street Name", "Neighborhood Name",
                "City Name", "State Name", mock(User.class));
        secondAddress.setId(2L);
        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        user.setAdresses(Set.of(firstAddress, secondAddress));
        user.setCart(new ArrayList<>());
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        when(authnService.getAuthnUser()).thenReturn(Optional.of(user));

        Set<AddressDTO> addressDTOSet = addressService.getUserAdresses();

        Assertions.assertEquals(user.getAdresses().size(), addressDTOSet.size());
        verify(authnService, times(1)).getAuthnUser();
    }

    @Test
    @DisplayName("Delete Successfully")
    void deleteAddress_successful() {
        var address = new Address("08510000", "House Number",
                "Street Name", "Neighborhood Name",
                "City Name", "State Name", mock(User.class));
        address.setPurchases(new HashSet<>());
        address.setId(1L);
        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        user.setAdresses(Set.of(address));
        user.setCart(new ArrayList<>());
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        when(authnService.getAuthnUser()).thenReturn(Optional.of(user));
        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));

        Assertions.assertDoesNotThrow(() -> addressService.deleteAddress(address.getId()));
        verify(addressRepository, times(1)).findById(address.getId());
        verify(authnService, times(1)).getAuthnUser();
    }
    @Test
    @DisplayName("Delete Unsuccessfully - Non Existent In List")
    void deleteAddress_unsuccessful_case01() {
        var address = new Address("08510000", "House Number",
                "Street Name", "Neighborhood Name",
                "City Name", "State Name", mock(User.class));
        address.setPurchases(new HashSet<>());
        address.setId(1L);
        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(new ArrayList<>());
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));
        when(authnService.getAuthnUser()).thenReturn(Optional.of(user));

        Assertions.assertThrows(NullAddressException.class, () -> addressService.deleteAddress(address.getId()));

        verify(addressRepository, times(1)).findById(address.getId());
        verify(authnService, times(1)).getAuthnUser();
    }
    @Test
    @DisplayName("Delete Unsuccessfully - Non Existent")
    void deleteAddress_unsuccessful_case02() {
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());
        when(authnService.getAuthnUser()).thenReturn(Optional.of(mock(User.class)));

        Assertions.assertThrows(NullAddressException.class, () -> addressService.deleteAddress(1L));

        verify(addressRepository, times(1)).findById(1L);
        verify(authnService, times(1)).getAuthnUser();
    }

    @Test
    @DisplayName("Update successfully")
    void updateAddress_successful() {
        var address = new Address("08510000", "House Number",
                "Street Name", "Neighborhood Name",
                "City Name", "State Name", mock(User.class));
        address.setPurchases(new HashSet<>());
        address.setId(1L);
        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        user.setAdresses(Set.of(address));
        user.setCart(new ArrayList<>());
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        when(authnService.getAuthnUser()).thenReturn(Optional.of(user));
        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));

        var data = new AddressDTO(
                1L, "08510000", "House Number",
                "Street Name", "Neighborhood Name",
                "City Name", "State Name");
        Assertions.assertDoesNotThrow(() -> addressService.updateAddress(data));
        verify(authnService, times(1)).getAuthnUser();
        verify(addressRepository, times(1)).findById(any(Long.class));
        verify(addressRepository, times(1)).save(any(Address.class));
    }
    @Test
    @DisplayName("Update successfully - NonExistent In List")
    void updateAddress_unsuccessful_case01() {
        var address = new Address("08510000", "House Number",
                "Street Name", "Neighborhood Name",
                "City Name", "State Name", mock(User.class));
        address.setPurchases(new HashSet<>());
        address.setId(1L);
        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(new ArrayList<>());
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        when(authnService.getAuthnUser()).thenReturn(Optional.of(user));
        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));

        var data = new AddressDTO(
                1L, "08510000", "House Number",
                "Street Name", "Neighborhood Name",
                "City Name", "State Name");
        Assertions.assertThrows(NullAddressException.class,
                () -> addressService.updateAddress(data));
        verify(authnService, times(1)).getAuthnUser();
        verify(addressRepository, times(1)).findById(any(Long.class));
        verify(addressRepository, never()).save(any(Address.class));
    }
    @Test
    @DisplayName("Update successfully - NonExistent In List")
    void updateAddress_unsuccessful_case02() {
        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(new ArrayList<>());
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        when(authnService.getAuthnUser()).thenReturn(Optional.of(user));
        when(addressRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        var data = new AddressDTO(
                1L, "08510000", "House Number",
                "Street Name", "Neighborhood Name",
                "City Name", "State Name");
        Assertions.assertThrows(NullAddressException.class,
                () -> addressService.updateAddress(data));
        verify(authnService, times(1)).getAuthnUser();
        verify(addressRepository, times(1)).findById(any(Long.class));
        verify(addressRepository, never()).save(any(Address.class));
    }
}