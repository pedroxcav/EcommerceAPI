package com.ecommerce.api.repository;

import com.ecommerce.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    UserDetails findByUsername(String username);

    @Query(value = "SELECT u FROM User u WHERE u.username = :username")
    Optional<User> loadByUsername(String username);

    boolean existsByUsernameOrCPF(String username, String CPF);
}
