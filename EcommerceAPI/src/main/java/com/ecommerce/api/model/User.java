package com.ecommerce.api.model;

import com.ecommerce.api.model.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@NoArgsConstructor
@ToString
@Getter
@Setter
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false, unique = true, length = 11)
    private String CPF;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private Role role;

    @OneToOne(mappedBy = "user")
    private Number number;
    @OneToMany(mappedBy = "user")
    private Set<Address> adresses;
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Order> cart;
    @ManyToMany(mappedBy = "users")
    private Set<Product> wishlist;
    @OneToMany(mappedBy = "user")
    private List<Purchase> purchases;

    public User(String name, String username, String CPF, String email, String password, Role role) {
        this.name = name;
        this.username = username;
        this.CPF = CPF;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.role == Role.ADMIN)
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_USER")
            );
        else
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
    public Set<Address> getAdresses() {
        return adresses.stream()
                .filter(Address::isActive)
                .collect(Collectors.toSet());
    }

    @PreRemove
    public void preRemove() {
        for (Address address : this.adresses) {
            address.setUser(null);
            address.setActive(false);
        }
        this.wishlist = null;
        this.purchases.forEach(purchase -> purchase.setUser(null));
    }
}
