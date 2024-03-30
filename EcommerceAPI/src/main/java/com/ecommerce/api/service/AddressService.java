package com.ecommerce.api.service;

import com.ecommerce.api.model.Address;
import com.ecommerce.api.model.User;
import com.ecommerce.api.model.dto.address.AddressRequestDTO;
import com.ecommerce.api.model.dto.address.AddressResponseDTO;
import com.ecommerce.api.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserService userService;

    public void newAdress(AddressRequestDTO addressData) {
        var user = new User(userService.getAuthUser());
        Address address = new Address(
                addressData.zipCode(),
                addressData.number(),
                addressData.street(),
                addressData.neighborhood(),
                addressData.city(),
                addressData.state(),
                user);
        user.getAdresses().add(address);
        addressRepository.save(address);
    }
    public Set<AddressResponseDTO> getUserAdresses() {
        var user = new User(userService.getAuthUser());
        return user.getAdresses().stream()
                .map(address -> new AddressResponseDTO(
                        address.getId(),
                        address.getZipCode(),
                        address.getNumber(),
                        address.getStreet(),
                        address.getNeighborhood(),
                        address.getCity(),
                        address.getState()
                )).collect(Collectors.toSet());
    }
    public void deleteAddress(Long id) {
        var user = new User(userService.getAuthUser());
        Optional<Address> optionalAddress = addressRepository.findById(id);
        if(optionalAddress.isPresent() && optionalAddress.get().isActive()) {
            Address address = optionalAddress.get();
            Set<Address> adresses = user.getAdresses();
            if(adresses.contains(optionalAddress.get())) {
                address.setActive(false);
                addressRepository.save(address);
            } else
                throw new RuntimeException("The address doesn't exist in your list!");
        } else
            throw new RuntimeException("The address doesn't exist!");
    }
}
