package com.ecommerce.api.model.dto.user;

public record RegistrationDTO(String name,
                              String username,
                              String CPF,
                              String email,
                              String password) {
}
