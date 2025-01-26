package com.UNN.xchange.Services;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.UNN.xchange.Models.RevenueData;
import com.UNN.xchange.Models.SalesData;
import com.UNN.xchange.Models.Seller;
import com.UNN.xchange.Repositories.SellerRepository;

@Service
public class SellerService implements UserDetailsService {

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public SellerService(SellerRepository sellerRepository, PasswordEncoder passwordEncoder) {
        this.sellerRepository = sellerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Seller seller = sellerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Seller not found with email: " + email));

        // Ensure the role is prefixed with "ROLE_" for Spring Security
        String role = seller.getRole().startsWith("ROLE_") ? seller.getRole() : "ROLE_" + seller.getRole();

        return new User(
                seller.getEmail(),
                seller.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(role))
        );
    }

    public Seller validateSeller(String email, String password) {
        Seller seller = sellerRepository.findByEmail(email) 
                .orElse(null);

        if (seller != null && passwordEncoder.matches(password, seller.getPassword())) {
            return seller;
        }
        return null;
    }

    public Map<String, String> registerSeller(String username, String email, String password) {
        // Check if the email is already registered
        if (sellerRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email is already registered");
        }

        // Create a new seller
        Seller seller = new Seller();
        seller.setUsername(username);
        seller.setEmail(email);
        seller.setPassword(passwordEncoder.encode(password));
        seller.setRole("ROLE_SELLER"); // Set the default role for sellers
        sellerRepository.save(seller);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Registration successful");
        return response;
    }

    public Seller findSellerByEmail(String email) {
        return sellerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Seller not found with email: " + email));
    }

    public Seller findSellerById(Long id) {
        return sellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seller not found with id: " + id));
    }

    public List<SalesData> getSalesData(Long sellerId) {
        Seller seller = unwrapSellar(sellerRepository.findById(sellerId), sellerId);
        return seller.getSalesData();
    }

    public List<RevenueData> getRevenueData(Long sellerId) {
        Seller seller = unwrapSellar(sellerRepository.findById(sellerId), sellerId);
        return seller.getRevenueData();
    }

    static Seller unwrapSellar(Optional<Seller> entity, Long id) {
        if (entity.isPresent()) {
            return entity.get();
        } else {
            throw new RuntimeException("Seller not found with id: " + id);
        }
    }

    // Other methods (update, delete, etc.) remain unchanged
}