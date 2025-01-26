package com.UNN.xchange.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.UNN.xchange.Models.Buyer;
import com.UNN.xchange.Repositories.BuyerRepository;
import com.UNN.xchange.Services.BuyerService;

@RestController
@RequestMapping("/buyers")
public class BuyerController {

    @Autowired
    private BuyerService buyerService;

    @Autowired
    private BuyerRepository buyerRepository;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerBuyer(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password) {

        buyerService.registerBuyer(username, email, password);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Registration successful");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginBuyer(
            @RequestParam String email,
            @RequestParam String password) {
    
        Buyer buyer = buyerService.validateBuyer(email, password);
        Map<String, Object> response = new HashMap<>();
    
        if (buyer != null) {
            response.put("message", "Login successful");
            response.put("redirectUrl", "/buyer_Dashboard.html"); // Redirect to buyer dashboard
            response.put("buyer", buyer); // Add the buyer object to the response
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Invalid email or password");
            return ResponseEntity.status(401).body(response);
        }
    }

   @GetMapping("/dashboard")
@PreAuthorize("hasRole('BUYER')") // Only buyers can access this endpoint
public ResponseEntity<Buyer> getBuyerDetails() {
    // Retrieve the authenticated user's email from the security context
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName(); // Get the email of the authenticated user

    // Fetch the buyer's details using the email
    Buyer buyer = buyerService.findBuyerByEmail(email);

    if (buyer != null) {
        // Return the buyer's details with a 200 OK status
        return ResponseEntity.ok(buyer);
    } else {
        // If the buyer is not found, return a 404 Not Found status
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<Buyer> getBuyerById(@PathVariable Long id) {
        return buyerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Only admins can access this endpoint
    public ResponseEntity<List<Buyer>> getAllBuyers() {
        List<Buyer> buyers = buyerRepository.findAll();
        return ResponseEntity.ok(buyers);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<Buyer> updateBuyer(@PathVariable Long id, @RequestBody Buyer updatedBuyer) {
        return buyerRepository.findById(id)
                .map(buyer -> {
                    buyer.setEmail(updatedBuyer.getEmail());
                    buyer.setOrders(updatedBuyer.getOrders());
                    Buyer savedBuyer = buyerRepository.save(buyer);
                    return ResponseEntity.ok(savedBuyer);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Only admins can delete buyers
    public ResponseEntity<Object> deleteBuyer(@PathVariable Long id) {
        return buyerRepository.findById(id)
                .map(buyer -> {
                    buyerRepository.delete(buyer);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}