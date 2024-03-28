package com.ecommerce.api.service;

import com.ecommerce.api.model.Address;
import com.ecommerce.api.model.User;
import com.ecommerce.api.model.dto.AddressRequestDTO;
import com.ecommerce.api.model.dto.AddressResponseDTO;
import com.ecommerce.api.repository.AddressRepository;
import com.ecommerce.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    public void newAdress(AddressRequestDTO addressData) {
        String username = userService.getAuthnUsername();
        var user = (User) userRepository.findByUsername(username);

        Address address = new Address(
                addressData.zipCode(), addressData.number(), addressData.street(), addressData.neighborhood(), addressData.city(), addressData.state(), user);
        addressRepository.save(address);
    }
    public List<AddressResponseDTO> getUserAdresses() {
        String username = userService.getAuthnUsername();
        User user = (User) userRepository.findByUsername(username);
        return user.getAddress().stream()
                .filter(Address::isActive)
                .map(address -> new AddressResponseDTO(
                        address.getId(),
                        address.getZipCode(),
                        address.getNumber(),
                        address.getStreet(),
                        address.getNeighborhood(),
                        address.getCity(),
                        address.getState()
                )).collect(Collectors.toList());
    }
    public void deleteAddress(Long id) {
        Optional<Address> optionalAddress = addressRepository.findById(id);
        if(optionalAddress.isPresent() && optionalAddress.get().isActive()) {
            Address address = optionalAddress.get();
            address.setActive(false);
            addressRepository.save(address);
        } else
            throw new RuntimeException("Address doesn't exist in your list!");
    }
}
