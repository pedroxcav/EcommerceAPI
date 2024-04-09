package com.ecommerce.api.service;

import com.ecommerce.api.exception.NullNumberException;
import com.ecommerce.api.exception.NumberRegisteredException;
import com.ecommerce.api.model.Number;
import com.ecommerce.api.model.User;
import com.ecommerce.api.model.dto.number.NumberDTO;
import com.ecommerce.api.repository.NumberRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NumberService {
    private final NumberRepository numberRepository;
    private final UserService userService;

    public NumberService(NumberRepository numberRepository, UserService userService) {
        this.numberRepository = numberRepository;
        this.userService = userService;
    }

    public void newNumber(NumberDTO data) {
        Optional<User> optionalUser = userService.getAuthnUser();
        optionalUser.ifPresent(user -> {
            if(user.getNumber() != null)
                throw new NumberRegisteredException();
            var number = new Number(
                    data.areaCode(),
                    data.number(),
                    user);
            numberRepository.save(number);
        });
    }
    public void deleteNumber() {
        Optional<User> optionalUser = userService.getAuthnUser();
        optionalUser.ifPresent(user -> {
            var number = user.getNumber();
            if(number != null)
                numberRepository.delete(number);
            else
                throw new NullNumberException();
        });
    }
    public void updateNumber(NumberDTO data) {
        Optional<User> optionalUser = userService.getAuthnUser();
        optionalUser.ifPresent(user -> {
            var number = user.getNumber();
            if(number != null) {
                number.setAreaCode(data.areaCode());
                number.setNumber(data.number());
                numberRepository.save(number);
            } else
                throw new NullNumberException();
        });
    }
    public NumberDTO getUserNumber() {
        Optional<User> optionalUser = userService.getAuthnUser();
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            var number = user.getNumber();
            if(number != null) {
                return new NumberDTO(
                        number.getAreaCode(),
                        number.getNumber()
                );
            } else
                throw new NullNumberException();
        } else
            return null;
    }
}
