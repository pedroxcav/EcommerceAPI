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
    private UserService userService;
    @InjectMocks
    private NumberService numberService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void newNumber_successful() {
        var user = new User(
                "Pedro Cavalcanti",
                "pedroxcav",
                "01010101010",
                "pedroxcav@icloud.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(new ArrayList<>());
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        when(userService.getAuthnUser()).thenReturn(Optional.of(user));

        numberService.newNumber(new NumberDTO("11", "910000000"));
        verify(numberRepository, times(1)).save(any(Number.class));
        verify(userService, times(1)).getAuthnUser();
    }
    @Test
    void newNumber_unsuccessful() {
        var user = new User(
                "Pedro Cavalcanti",
                "pedroxcav",
                "01010101010",
                "pedroxcav@icloud.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(new ArrayList<>());
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        user.setNumber(new Number("11","910000000", user));
        when(userService.getAuthnUser()).thenReturn(Optional.of(user));

        Assertions.assertThrows(NumberRegisteredException.class, () -> numberService.newNumber(new NumberDTO("11","910000000")));
        verify(userService, times(1)).getAuthnUser();
        verify(numberRepository, never()).save(any(Number.class));
    }

    @Test
    void deleteNumber_successful() {
        var user = new User(
                "Pedro Cavalcanti",
                "pedroxcav",
                "01010101010",
                "pedroxcav@icloud.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(new ArrayList<>());
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        user.setNumber(new Number("11","910000000", user));
        when(userService.getAuthnUser()).thenReturn(Optional.of(user));

        Assertions.assertDoesNotThrow(() -> numberService.deleteNumber());
        verify(userService, times(1)).getAuthnUser();
        verify(numberRepository, times(1)).delete(any(Number.class));
    }
    @Test
    void deleteNumber_unsuccessful() {
        var user = new User(
                "Pedro Cavalcanti",
                "pedroxcav",
                "01010101010",
                "pedroxcav@icloud.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(new ArrayList<>());
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        when(userService.getAuthnUser()).thenReturn(Optional.of(user));

        Assertions.assertThrows(NullNumberException.class, () -> numberService.deleteNumber());
        verify(userService, times(1)).getAuthnUser();
        verify(numberRepository, never()).delete(any(Number.class));
    }

    @Test
    void updateNumber_successful() {
        var user = new User(
                "Pedro Cavalcanti",
                "pedroxcav",
                "01010101010",
                "pedroxcav@icloud.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(new ArrayList<>());
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        user.setNumber(new Number("11","910000000", user));
        when(userService.getAuthnUser()).thenReturn(Optional.of(user));

        Assertions.assertDoesNotThrow(() -> numberService.updateNumber(new NumberDTO("11","910000000")));
        verify(numberRepository, times(1)).save(any(Number.class));
        verify(userService, times(1)).getAuthnUser();
    }
    @Test
    void updateNumber_unsuccessful() {
        var user = new User(
                "Pedro Cavalcanti",
                "pedroxcav",
                "01010101010",
                "pedroxcav@icloud.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(new ArrayList<>());
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        when(userService.getAuthnUser()).thenReturn(Optional.of(user));

        Assertions.assertThrows(NullNumberException.class, () -> numberService.updateNumber(new NumberDTO("11","910000000")));
        verify(userService, times(1)).getAuthnUser();
        verify(numberRepository, never()).save(any(Number.class));
    }

    @Test
    void getUserNumber_successful() {
        var user = new User(
                "Pedro Cavalcanti",
                "pedroxcav",
                "01010101010",
                "pedroxcav@icloud.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(new ArrayList<>());
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        user.setNumber(new Number("11","910000000", user));
        when(userService.getAuthnUser()).thenReturn(Optional.of(user));

        NumberDTO userNumber = numberService.getUserNumber();
        Assertions.assertEquals(user.getNumber().getAreaCode(), userNumber.areaCode());
        Assertions.assertEquals(user.getNumber().getNumber(), userNumber.number());
        verify(userService, times(1)).getAuthnUser();
    }
    @Test
    void getUserNumber_unsuccessful() {
        var user = new User(
                "Pedro Cavalcanti",
                "pedroxcav",
                "01010101010",
                "pedroxcav@icloud.com",
                "1234", Role.ADMIN);
        user.setAdresses(new HashSet<>());
        user.setCart(new ArrayList<>());
        user.setWishlist(new HashSet<>());
        user.setPurchases(new ArrayList<>());
        when(userService.getAuthnUser()).thenReturn(Optional.of(user));

        Assertions.assertThrows(NullNumberException.class, () -> numberService.getUserNumber());
        verify(userService, times(1)).getAuthnUser();
    }
}