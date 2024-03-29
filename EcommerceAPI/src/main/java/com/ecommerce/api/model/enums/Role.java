package com.ecommerce.api.model.enums;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("admin"),
    USER("user");
    private final String role;
    Role(String role) {
        this.role = role;
    }
}
