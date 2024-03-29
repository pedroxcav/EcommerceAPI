package com.ecommerce.api.model.dto.user;

import com.ecommerce.api.model.*;
import com.ecommerce.api.model.Number;
import com.ecommerce.api.model.enums.Role;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record UserResponseDTO(UUID id,
                              String name,
                              String username,
                              String CPF,
                              String email,
                              String password,
                              Role role,
                              Number number,
                              Set<Address> address,
                              List<Order> cart,
                              Set<Product> wishlist,
                              List<Purchase> purchases) {
}
