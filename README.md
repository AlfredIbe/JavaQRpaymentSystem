//Seller Repository
package com.UNN.xchange.Repositories;

import com.UNN.xchange.Models.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller>findByEmail(String email); // Find seller by email
}
//Buyer Repository
package com.UNN.xchange.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.UNN.xchange.Models.Buyer;

@Repository
public interface BuyerRepository extends JpaRepository<Buyer, Long> {
    <Optional>Buyer findByEmail(String email);
}
package com.UNN.xchange.Services;

import java.util.Collections;
import java.util.List;
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
    private final SellerRepository sellerRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

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

    public Seller registerSeller(String username, String email, String password) {
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
        return sellerRepository.save(seller);
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

    public void addBalance(String sellerId, double amount) {
        Long sellerIdLong = Long.parseLong(sellerId);
        Seller seller = sellerRepository.findById(sellerIdLong)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        seller.setBalance(seller.getBalance() + amount);
        sellerRepository.save(seller);
    }

    // Add existsById method
    public boolean existsById(String sellerId) {
        return sellerRepository.existsById(Long.valueOf(sellerId));
    }
}

package com.UNN.xchange.Services;

import java.util.Collections;
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
    private final BuyerRepository buyerRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

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

    public Buyer registerBuyer(String username, String email, String password) {
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
        
        return buyerRepository.save(buyer);
    }

    public Buyer findBuyerByEmail(String email) {
        return buyerRepository.findByEmail(email);
    }

    public Buyer findBuyerById(Long id) {
        return buyerRepository.findById(id).orElse(null);
    }

    public void deductBalance(String buyerId, double amount) {
        Buyer buyer = buyerRepository.findById(Long.valueOf(buyerId))
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        if (buyer.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        buyer.setBalance(buyer.getBalance() - amount);
        buyerRepository.save(buyer);
    }

    // Add existsById method
    public boolean existsById(String buyerId) {
        return buyerRepository.existsById(Long.valueOf(buyerId));
    }
}
