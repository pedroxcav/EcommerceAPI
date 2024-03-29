package com.ecommerce.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "numbers")
@NoArgsConstructor
@ToString
@Getter
@Setter
public class Number {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String areaCode;
    @Column(nullable = false, unique = true)
    private String number;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Number(String areaCode, String number, User user) {
        this.areaCode = areaCode;
        this.number = number;
        this.user = user;
    }
}
