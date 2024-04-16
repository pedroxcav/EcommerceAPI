package com.ecommerce.api.model.dto.address;

public record AddressDTO(Long id,
                         String zipCode,
                         String number,
                         String street,
                         String neighborhood,
                         String city,
                         String state) {
}
