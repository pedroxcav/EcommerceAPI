package com.ecommerce.api.service;

import com.ecommerce.api.model.Number;
import com.ecommerce.api.model.User;
import com.ecommerce.api.model.dto.number.NumberDTO;
import com.ecommerce.api.repository.NumberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NumberService {
    @Autowired
    private NumberRepository numberRepository;
    @Autowired
    private UserService userService;

    public void newNumber(NumberDTO data) {
        var user = new User(userService.getAuthUser());
        if(user.getNumber() != null)
            throw new RuntimeException("There's a number registered already!");
        var number = new Number(
                data.areaCode(),
                data.number(),
                user);
        numberRepository.save(number);
    }
    public void deleteNumber() {
        var user = new User(userService.getAuthUser());
        var number = user.getNumber();
        if(number != null)
            numberRepository.delete(number);
        else
            throw new RuntimeException("You don't have a number yet!");
    }
    public void updateNumber(NumberDTO data) {
        var user = new User(userService.getAuthUser());
        var number = user.getNumber();
        if(number != null) {
            number.setAreaCode(data.areaCode());
            number.setNumber(data.number());
            numberRepository.save(number);
        } else
            throw new RuntimeException("You don't have a number yet!");
    }
    public NumberDTO getUserNumber() {
        var user = new User(userService.getAuthUser());
        var number = user.getNumber();
        if(number != null) {
            return new NumberDTO(
                    number.getAreaCode(),
                    number.getNumber()
            );
        } else
            throw new RuntimeException("You don't have a number yet!");
    }
}
