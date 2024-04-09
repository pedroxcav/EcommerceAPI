package com.ecommerce.api.service;

import com.ecommerce.api.exception.NullAddressException;
import com.ecommerce.api.model.Address;
import com.ecommerce.api.model.User;
import com.ecommerce.api.model.dto.address.AddressRequestDTO;
import com.ecommerce.api.model.dto.address.AddressResponseDTO;
import com.ecommerce.api.repository.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserService userService;

    public AddressService(AddressRepository addressRepository, UserService userService) {
        this.addressRepository = addressRepository;
        this.userService = userService;
    }

    public void newAddress(AddressRequestDTO data) {
        Optional<User> optionalUser = userService.getAuthnUser();
        optionalUser.ifPresent(user -> {
            Address address = new Address(
                    data.zipCode(),
                    data.number(),
                    data.street(),
                    data.neighborhood(),
                    data.city(),
                    data.state(),
                    user);
            user.getFilteredAdresses().add(address);
            addressRepository.save(address);
        });
    }
    public Set<AddressResponseDTO> getUserAdresses() {
        Optional<User> optionalUser = userService.getAuthnUser();
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            return user.getFilteredAdresses().stream()
                    .map(address -> new AddressResponseDTO(
                            address.getId(),
                            address.getZipCode(),
                            address.getNumber(),
                            address.getStreet(),
                            address.getNeighborhood(),
                            address.getCity(),
                            address.getState()
                    )).collect(Collectors.toSet());
        } else
            return null;
    }
    public void deleteAddress(Long id) {
        Optional<User> optionalUser = userService.getAuthnUser();
        optionalUser.ifPresent(user -> {
            Optional<Address> optionalAddress = addressRepository.findById(id);
            if(optionalAddress.isPresent() && optionalAddress.get().isActive()) {
                Address address = optionalAddress.get();
                Set<Address> adresses = user.getFilteredAdresses();
                if(adresses.contains(optionalAddress.get())) {
                    if(!address.getPurchases().isEmpty()) {
                        address.setActive(false);
                        addressRepository.save(address);
                    } else
                        addressRepository.delete(address);
                } else
                    throw new NullAddressException("The address doesn't exist in your list!");
            } else
                throw new NullAddressException();
        });
    }
}
