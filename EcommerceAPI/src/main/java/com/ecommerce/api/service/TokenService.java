package com.ecommerce.api.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.ecommerce.api.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer("EcommerceAPI")
                .withSubject(user.getUsername())
                .withExpiresAt(generateExpiration())
                .sign(algorithm);
    }

    public String validateToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.require(algorithm)
                .withIssuer("EcommerceAPI")
                .build()
                .verify(token)
                .getSubject();
    }

    private Instant generateExpiration() {
        return LocalDateTime.now().plusHours(1L).toInstant(ZoneOffset.of("-03:00"));
    }
}
