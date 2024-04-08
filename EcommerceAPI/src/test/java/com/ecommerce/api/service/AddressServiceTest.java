package com.ecommerce.api.service;

import com.ecommerce.api.exception.NullAddressException;
import com.ecommerce.api.model.*;
import com.ecommerce.api.model.Number;
import com.ecommerce.api.model.dto.address.AddressRequestDTO;
import com.ecommerce.api.model.dto.address.AddressResponseDTO;
import com.ecommerce.api.model.dto.user.UserResponseDTO;
import com.ecommerce.api.model.enums.Role;
import com.ecommerce.api.repository.AddressRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
    private UserService userService;
    @InjectMocks
    private AddressService addressService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void newAddress_successful() {
        var data = new AddressRequestDTO(
                "08510000", "10",
                "streetName", "neighborhoodName",
                "cityName", "stateName");
        Set<Address> adresses = new HashSet<>();
        List<Order> cart = new ArrayList<>();
        Set<Product> wishlist = new HashSet<>();
        List<Purchase> purchases = new ArrayList<>();
        var number = new Number("11", "910000000", mock(User.class));
        when(userService.getAuthUser()).thenReturn(
                new UserResponseDTO(null,"Pedro Cavalcanti", "pedroxcav",
                        "01010101010","pedroxcav@icloud.com","1234",
                        Role.ADMIN, number, adresses, cart, wishlist, purchases));

        addressService.newAddress(data);

        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void getUserAdresses_successful() {
        var firstAddress = new Address("08510000", "10",
                "streetName", "neighborhoodName",
                "cityName", "stateName", mock(User.class));
        firstAddress.setId(1L);
        var secondAddress = new Address("08510000", "10",
                "streetName", "neighborhoodName",
                "cityName", "stateName", mock(User.class));
        secondAddress.setId(2L);
        Set<Address> adresses = Set.of(firstAddress, secondAddress);
        List<Order> cart = new ArrayList<>();
        Set<Product> wishlist = new HashSet<>();
        List<Purchase> purchases = new ArrayList<>();
        var number = new Number("11", "910000000", mock(User.class));
        when(userService.getAuthUser()).thenReturn(
                new UserResponseDTO(null,"Pedro Cavalcanti", "pedroxcav",
                        "01010101010","pedroxcav@icloud.com","1234",
                        Role.ADMIN, number, adresses, cart, wishlist, purchases));

        Set<AddressResponseDTO> addressResponseDTOSet = addressService.getUserAdresses();

        Assertions.assertEquals(adresses.size(), addressResponseDTOSet.size());
        verify(userService, times(1)).getAuthUser();
    }

    @Test
    void deleteAddress_successful() {
        var address = new Address("08510000", "10",
                "streetName", "neighborhoodName",
                "cityName", "stateName", mock(User.class));
        address.setPurchases(new HashSet<>());
        address.setId(1L);
        Set<Address> adresses = Set.of(address);
        List<Order> cart = new ArrayList<>();
        Set<Product> wishlist = new HashSet<>();
        List<Purchase> purchases = new ArrayList<>();
        var number = new Number("11", "910000000", mock(User.class));
        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));
        when(userService.getAuthUser()).thenReturn(
                new UserResponseDTO(null,"Pedro Cavalcanti", "pedroxcav",
                        "01010101010","pedroxcav@icloud.com","1234",
                        Role.ADMIN, number, adresses, cart, wishlist, purchases));

        Assertions.assertDoesNotThrow(() -> addressService.deleteAddress(address.getId()));
        verify(addressRepository, times(1)).findById(address.getId());
        verify(userService, times(1)).getAuthUser();
    }
    @Test
    void deleteAddress_unsuccessful_case01() {
        var address = new Address("08510000", "10",
                "streetName", "neighborhoodName",
                "cityName", "stateName", mock(User.class));
        address.setPurchases(new HashSet<>());
        address.setId(1L);
        Set<Address> adresses = new HashSet<>();
        List<Order> cart = new ArrayList<>();
        Set<Product> wishlist = new HashSet<>();
        List<Purchase> purchases = new ArrayList<>();
        var number = new Number("11", "910000000", mock(User.class));
        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));
        when(userService.getAuthUser()).thenReturn(
                new UserResponseDTO(null,"Pedro Cavalcanti", "pedroxcav",
                        "01010101010","pedroxcav@icloud.com","1234",
                        Role.ADMIN, number, adresses, cart, wishlist, purchases));

        Assertions.assertThrows(NullAddressException.class, () -> addressService.deleteAddress(address.getId()));

        verify(addressRepository, times(1)).findById(address.getId());
        verify(userService, times(1)).getAuthUser();
    }
    @Test
    void deleteAddress_unsuccessful_case02() {
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());
        when(userService.getAuthUser()).thenReturn(mock(UserResponseDTO.class));

        Assertions.assertThrows(NullAddressException.class, () -> addressService.deleteAddress(1L));

        verify(addressRepository, times(1)).findById(1L);
        verify(userService, times(1)).getAuthUser();
    }
}