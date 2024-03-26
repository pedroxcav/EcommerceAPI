package com.ecommerce.api.model.dto;

import com.ecommerce.api.model.enums.Role;

public record Registration(String name, String username, String CPF, String email, String password) {
}
