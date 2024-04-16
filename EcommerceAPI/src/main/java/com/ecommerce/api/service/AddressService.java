package com.ecommerce.api.service;

import com.ecommerce.api.exception.NullAddressException;
import com.ecommerce.api.model.Address;
import com.ecommerce.api.model.User;
import com.ecommerce.api.model.dto.address.AddressRequestDTO;
import com.ecommerce.api.model.dto.address.AddressDTO;
import com.ecommerce.api.repository.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final AuthnService authnService;

    public AddressService(AddressRepository addressRepository, AuthnService authnService) {
        this.addressRepository = addressRepository;
        this.authnService = authnService;
    }

    public void newAddress(AddressRequestDTO data) {
        Optional<User> optionalUser = authnService.getAuthnUser();
        optionalUser.ifPresent(user -> {
            Address address = new Address(
                    data.zipCode(),
                    data.number(),
                    data.street(),
                    data.neighborhood(),
                    data.city(),
                    data.state(),
                    user);
            user.getActiveAdresses().add(address);
            addressRepository.save(address);
        });
    }
    public Set<AddressDTO> getUserAdresses() {
        Optional<User> optionalUser = authnService.getAuthnUser();
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            return user.getActiveAdresses().stream()
                    .map(address -> new AddressDTO(
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
        Optional<User> optionalUser = authnService.getAuthnUser();
        optionalUser.ifPresent(user -> {
            Optional<Address> optionalAddress = addressRepository.findById(id);
            if(optionalAddress.isPresent() && optionalAddress.get().isActive()) {
                Address address = optionalAddress.get();
                Set<Address> adresses = user.getActiveAdresses();
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
    public void updateAddress(AddressDTO data) {
        Optional<User> optionalUser = authnService.getAuthnUser();
        optionalUser.ifPresent(user -> {
            Optional<Address> optionalAddress = addressRepository.findById(data.id());
            if (optionalAddress.isPresent()){
                Address address = optionalAddress.get();
                if (user.getAdresses().contains(address)) {
                    address.setZipCode(data.zipCode());
                    address.setNumber(data.number());
                    address.setStreet(data.street());
                    address.setNeighborhood(data.neighborhood());
                    address.setCity(data.city());
                    address.setState(data.state());
                    addressRepository.save(address);
                } else
                    throw new NullAddressException("The address doesn't exist in your list!");
            } else
                throw new NullAddressException();
        });
    }
}
