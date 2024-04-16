package com.ecommerce.api.service;

import com.ecommerce.api.exception.NullNumberException;
import com.ecommerce.api.exception.NumberRegisteredException;
import com.ecommerce.api.model.*;
import com.ecommerce.api.model.Number;
import com.ecommerce.api.model.dto.number.NumberDTO;
import com.ecommerce.api.model.enums.Role;
import com.ecommerce.api.repository.NumberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.mockito.Mockito.*;

class NumberServiceTest {
    @Mock
    private NumberRepository numberRepository;
    @Mock
    private AuthnService authnService;
    @InjectMocks
    private NumberService numberService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Register Number Successfully")
    void newNumber_successful() {
        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(new ArrayList<>());
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        when(authnService.getAuthnUser()).thenReturn(Optional.of(user));

        numberService.newNumber(new NumberDTO("11", "910000000"));
        verify(numberRepository, times(1)).save(any(Number.class));
        verify(authnService, times(1)).getAuthnUser();
    }
    @Test
    @DisplayName("Number Already Registered")
    void newNumber_unsuccessful() {
        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(new ArrayList<>());
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        user.setNumber(new Number("11","910000000", user));
        when(authnService.getAuthnUser()).thenReturn(Optional.of(user));

        Assertions.assertThrows(NumberRegisteredException.class, () -> numberService.newNumber(new NumberDTO("11","910000000")));
        verify(authnService, times(1)).getAuthnUser();
        verify(numberRepository, never()).save(any(Number.class));
    }

    @Test
    @DisplayName("Delete Successfully")
    void deleteNumber_successful() {
        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(new ArrayList<>());
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        user.setNumber(new Number("11","910000000", user));
        when(authnService.getAuthnUser()).thenReturn(Optional.of(user));

        Assertions.assertDoesNotThrow(() -> numberService.deleteNumber());
        verify(authnService, times(1)).getAuthnUser();
        verify(numberRepository, times(1)).delete(any(Number.class));
    }
    @Test
    @DisplayName("Delete Unsuccessfully - NonExistent")
    void deleteNumber_unsuccessful() {
        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(new ArrayList<>());
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        when(authnService.getAuthnUser()).thenReturn(Optional.of(user));

        Assertions.assertThrows(NullNumberException.class, () -> numberService.deleteNumber());
        verify(authnService, times(1)).getAuthnUser();
        verify(numberRepository, never()).delete(any(Number.class));
    }

    @Test
    @DisplayName("Update Successfully")
    void updateNumber_successful() {
        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(new ArrayList<>());
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        user.setNumber(new Number("11","910000000", user));
        when(authnService.getAuthnUser()).thenReturn(Optional.of(user));

        Assertions.assertDoesNotThrow(() -> numberService.updateNumber(new NumberDTO("11","910000000")));
        verify(numberRepository, times(1)).save(any(Number.class));
        verify(authnService, times(1)).getAuthnUser();
    }
    @Test
    @DisplayName("Update Unsuccessfully - NonExistent")
    void updateNumber_unsuccessful() {
        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(new ArrayList<>());
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        when(authnService.getAuthnUser()).thenReturn(Optional.of(user));

        Assertions.assertThrows(
                NullNumberException.class,
                () -> numberService.updateNumber(new NumberDTO("11","910000000")));
        verify(authnService, times(1)).getAuthnUser();
        verify(numberRepository, never()).save(any(Number.class));
    }

    @Test
    @DisplayName("Get User Number Successfully")
    void getUserNumber_successful() {
        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(new ArrayList<>());
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        user.setNumber(new Number("11","910000000", user));
        when(authnService.getAuthnUser()).thenReturn(Optional.of(user));

        NumberDTO userNumber = numberService.getUserNumber();
        Assertions.assertEquals(user.getNumber().getAreaCode(), userNumber.areaCode());
        Assertions.assertEquals(user.getNumber().getNumber(), userNumber.number());
        verify(authnService, times(1)).getAuthnUser();
    }
    @Test
    @DisplayName("Get User Number Unsuccessfully - NonExistent")
    void getUserNumber_unsuccessful() {
        var user = new User("Admin", "admin",
                "24512127801", "admin@gmail.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(new ArrayList<>());
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        when(authnService.getAuthnUser()).thenReturn(Optional.of(user));

        Assertions.assertThrows(NullNumberException.class, () -> numberService.getUserNumber());
        verify(authnService, times(1)).getAuthnUser();
    }
}