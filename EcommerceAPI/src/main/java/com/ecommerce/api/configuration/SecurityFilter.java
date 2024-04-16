package com.ecommerce.api.configuration;

import com.ecommerce.api.exception.NullUserException;
import com.ecommerce.api.repository.UserRepository;
import com.ecommerce.api.service.AuthnService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;
    private final AuthnService authnService;

    public SecurityFilter(UserRepository userRepository, AuthnService authnService) {
        this.userRepository = userRepository;
        this.authnService = authnService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = recoverToken(request);
        if (token != null) {
            String username = authnService.validateToken(token);
            UserDetails user = userRepository.findByUsername(username);
            if (user == null) throw new NullUserException("User login not found!");

            var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader == null)
            return null;
        return authorizationHeader.replace("Bearer ","");
    }
}
