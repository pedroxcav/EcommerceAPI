package com.ecommerce.api.service;

import com.ecommerce.api.exception.NullAddressException;
import com.ecommerce.api.exception.NullOrderException;
import com.ecommerce.api.model.Address;
import com.ecommerce.api.model.Order;
import com.ecommerce.api.model.Purchase;
import com.ecommerce.api.model.User;
import com.ecommerce.api.model.dto.purchase.PurchaseRequestDTO;
import com.ecommerce.api.model.dto.purchase.PurchaseResponseDTO;
import com.ecommerce.api.repository.AddressRepository;
import com.ecommerce.api.repository.OrderRepository;
import com.ecommerce.api.repository.PurchaseRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final UserService userService;

    public PurchaseService(PurchaseRepository purchaseRepository, OrderRepository orderRepository, AddressRepository addressRepository, UserService userService) {
        this.purchaseRepository = purchaseRepository;
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
        this.userService = userService;
    }

    public void buyOrders(PurchaseRequestDTO data) {
        var user = new User(userService.getAuthUser());

        Optional<Address> optionalAddress = addressRepository.findById(data.addressId());
        if(optionalAddress.isEmpty() || !user.getFilteredAdresses().contains(optionalAddress.get()))
            throw new NullAddressException();

        double totalPrice = 0D;
        Set<Order> orderList = new HashSet<>();
        for (Long id : data.orderIdSet()) {
            Optional<Order> optionalOrder = orderRepository.findById(id);
            if(optionalOrder.isEmpty())
                throw new NullOrderException("One of the orders doesn't exist!");
            else {
                Order order = optionalOrder.get();
                totalPrice += order.getPrice();
                order.setCompleted(true);
                orderList.add(order);
            }
        }
        Purchase purchase = new Purchase(orderList, totalPrice, optionalAddress.get(), user);
        orderList.forEach(order -> order.setPurchase(purchase));
        purchaseRepository.save(purchase);
    }
    public Set<PurchaseResponseDTO> getUserPurchases() {
        var user = new User(userService.getAuthUser());
        return user.getPurchases().stream()
                .map(purchase -> new PurchaseResponseDTO(
                        purchase.getId(),
                        purchase.getTotalPrice(),
                        purchase.getOrders(),
                        purchase.getAddress()
                ))
                .collect(Collectors.toSet());
    }
    public Set<PurchaseResponseDTO> getAllPurchases() {
        List<Purchase> purchasesList = purchaseRepository.findAll();
        return purchasesList.stream()
                .map(purchase -> new PurchaseResponseDTO(
                        purchase.getId(),
                        purchase.getTotalPrice(),
                        purchase.getOrders(),
                        purchase.getAddress()
                ))
                .collect(Collectors.toSet());
    }
}
