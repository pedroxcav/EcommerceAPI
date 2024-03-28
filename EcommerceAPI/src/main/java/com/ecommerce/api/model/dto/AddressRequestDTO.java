package com.ecommerce.api.model.dto;

public record AddressRequestDTO(String zipCode,
                                String number,
                                String street,
                                String neighborhood,
                                String city,
                                String state) {
}
