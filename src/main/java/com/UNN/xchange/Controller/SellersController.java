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

import com.UNN.xchange.Models.Seller;
import com.UNN.xchange.Repositories.SellerRepository;
import com.UNN.xchange.Services.SellerService;

@RestController
@RequestMapping("/sellers")
public class SellersController {

    @Autowired
    private SellerService sellerService;

    @Autowired
    private SellerRepository sellerRepository;

    // Register a new seller
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerSeller(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password) {

        sellerService.registerSeller(username, email, password);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Registration successful");
        return ResponseEntity.ok(response);
    }

    // Login for sellers
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginSeller(
            @RequestParam String email,
            @RequestParam String password) {

        Seller seller = sellerService.validateSeller(email, password);
        Map<String, Object> response = new HashMap<>();

        if (seller != null) {
            response.put("message", "Login successful");
            response.put("redirectUrl", "/seller_Dashboard.html"); // Redirect to seller dashboard
            response.put("seller", seller); // Add the seller object to the response
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Invalid email or password");
            return ResponseEntity.status(401).body(response);
        }
    }

    // Get seller details for the authenticated seller
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('SELLER')") // Only sellers can access this endpoint
    public ResponseEntity<Seller> getSellerDetails() {
        // Retrieve the authenticated user's email from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Get the email of the authenticated user

        // Fetch the seller's details using the email
        Seller seller = sellerService.findSellerByEmail(email);

        if (seller != null) {
            // Return the seller's details with a 200 OK status
            return ResponseEntity.ok(seller);
        } else {
            // If the seller is not found, return a 404 Not Found status
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Get seller by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Seller> getSellerById(@PathVariable Long id) {
        return sellerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all sellers (only accessible by admins)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Only admins can access this endpoint
    public ResponseEntity<List<Seller>> getAllSellers() {
        List<Seller> sellers = sellerRepository.findAll();
        return ResponseEntity.ok(sellers);
    }

    // Update seller details
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Seller> updateSeller(@PathVariable Long id, @RequestBody Seller updatedSeller) {
        return sellerRepository.findById(id)
                .map(seller -> {
                    seller.setEmail(updatedSeller.getEmail());
                    seller.setProducts(updatedSeller.getProducts());
                    Seller savedSeller = sellerRepository.save(seller);
                    return ResponseEntity.ok(savedSeller);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete a seller (only accessible by admins)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Only admins can delete sellers
    public ResponseEntity<Object> deleteSeller(@PathVariable Long id) {
        return sellerRepository.findById(id)
                .map(seller -> {
                    sellerRepository.delete(seller);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}