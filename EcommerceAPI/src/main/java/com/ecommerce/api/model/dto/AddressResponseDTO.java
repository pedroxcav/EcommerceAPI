package com.ecommerce.api.model.dto;

public record AddressResponseDTO(Long id,
                                 String zipCode,
                                 String number,
                                 String street,
                                 String neighborhood,
                                 String city,
                                 String state) {
}
