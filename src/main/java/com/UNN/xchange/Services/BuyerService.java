package com.UNN.xchange.Services;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.UNN.xchange.Models.Buyer;
import com.UNN.xchange.Repositories.BuyerRepository;

@Service
public class BuyerService implements UserDetailsService {

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public BuyerService(BuyerRepository buyerRepository, PasswordEncoder passwordEncoder) {
        this.buyerRepository = buyerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Buyer buyer = buyerRepository.findByEmail(email);

        if (buyer == null) {
            throw new UsernameNotFoundException("Buyer not found with email: " + email);
        }

        // Ensure the role is prefixed with "ROLE_" for Spring Security
        String role = buyer.getRole().startsWith("ROLE_") ? buyer.getRole() : "ROLE_" + buyer.getRole();

        return new User(
                buyer.getEmail(),
                buyer.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(role))
        );
    }

    public Buyer validateBuyer(String email, String password) {
        Buyer buyer = buyerRepository.findByEmail(email);

        if (buyer != null && passwordEncoder.matches(password, buyer.getPassword())) {
            return buyer;
        }
        return null;
    }

    public Map<String, String> registerBuyer(String username, String email, String password) {
        // Check if the email is already registered
        if (buyerRepository.findByEmail(email) != null) {
            throw new RuntimeException("Email is already registered");
        }

        // Create a new buyer
        Buyer buyer = new Buyer();
        buyer.setUsername(username);
        buyer.setEmail(email);
        buyer.setPassword(passwordEncoder.encode(password));
        buyer.setRole("ROLE_BUYER"); // Set the default role for buyers
        buyerRepository.save(buyer);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Registration successful");
        return response;
    }

    public Buyer findBuyerByEmail(String email) {
        return buyerRepository.findByEmail(email);
    }

    public Buyer findBuyerById(Long id) {
        return buyerRepository.findById(id).orElse(null);
    }

    // Other methods (update, delete, etc.) remain unchanged
}