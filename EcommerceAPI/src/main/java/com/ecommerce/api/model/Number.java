package com.ecommerce.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "numbers")
@Getter
@Setter
public class Number {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String areaCode;
    @Column(nullable = false, unique = true)
    private String number;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
