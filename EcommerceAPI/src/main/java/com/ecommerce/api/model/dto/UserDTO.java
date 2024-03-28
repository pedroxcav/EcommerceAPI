package com.ecommerce.api.model.dto;

import com.ecommerce.api.model.Address;
import com.ecommerce.api.model.Number;
import com.ecommerce.api.model.Product;
import com.ecommerce.api.model.enums.Role;

import java.util.List;
import java.util.Set;

public record UserDTO(String name,
                      String username,
                      String CPF,
                      String email,
                      String password,
                      Role role,
                      Number number,
                      Set<Address> address,
                      List<Product> cart,
                      Set<Product> wishlist,
                      List<Product> purchases) {
}
