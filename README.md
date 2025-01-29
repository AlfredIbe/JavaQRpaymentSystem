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
package com.UNN.xchange.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.UNN.xchange.Models.Transaction;
import com.UNN.xchange.Repositories.TransactionRepository;
import com.UNN.xchange.Services.BuyerService;
import com.UNN.xchange.Services.QRCodeGenerator;
import com.UNN.xchange.Services.SellerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private BuyerService buyerService;

    @Autowired
    private SellerService sellerService;

    @Autowired
    private TransactionRepository transactionRepository;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Endpoint to generate a QR code for the cart items and seller ID.
     *
     * @param request A map containing the seller ID and list of cart items.
     * @return A ResponseEntity containing the QR code image as a Base64-encoded string.
     */
    @PostMapping("/generateQR")
    public ResponseEntity<String> generateQR(@RequestBody Map<String, Object> request) {
        try {
            // Extract seller ID and cart items from the request
            String sellerId = (String) request.get("sellerId");
            List<Map<String, Object>> cartItems = (List<Map<String, Object>>) request.get("cartItems");

            // Validate input
            if (sellerId == null || sellerId.isEmpty() || cartItems == null || cartItems.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                     .body("Seller ID and cart items are required");
            }

            // Create a structured QR code data object
            QRCodeData qrCodeData = new QRCodeData();
            qrCodeData.setSellerId(sellerId);
            qrCodeData.setCartItems(cartItems);

            // Convert the QR code data to a JSON string
            String qrData = objectMapper.writeValueAsString(qrCodeData);

            // Generate the QR code image
            String qrCodeImage = QRCodeGenerator.generateQRCodeImage(qrData);

            // Return the QR code image as a Base64-encoded string
            return ResponseEntity.ok(qrCodeImage);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body("Invalid cart items format");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error generating QR code: " + e.getMessage());
        }
    }

    /**
     * Endpoint to process a payment using the QR code data.
     *
     * @param request A map containing the buyer ID and QR code data.
     * @return A ResponseEntity containing the transaction details or an error message.
     */
    @PostMapping("/processPayment")
    @Transactional
    public ResponseEntity<?> processPayment(@RequestBody Map<String, Object> request) {
        try {
            // Extract buyer ID and QR code data from the request
            String buyerId = (String) request.get("buyerId");
            String qrData = (String) request.get("qrData");

            // Validate input
            if (buyerId == null || buyerId.isEmpty() || qrData == null || qrData.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                     .body("Buyer ID and QR code data are required");
            }

            // Parse the QR code data into a structured object
            QRCodeData qrCodeData = objectMapper.readValue(qrData, QRCodeData.class);

            // Validate seller ID and cart items
            String sellerId = qrCodeData.getSellerId();
            List<Map<String, Object>> cartItems = qrCodeData.getCartItems();
            if (sellerId == null || sellerId.isEmpty() || cartItems == null || cartItems.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                     .body("Invalid QR code data: seller ID or cart items missing");
            }

            // Calculate the total amount from the cart items
            double totalAmount = cartItems.stream()
                    .mapToDouble(item -> (double) item.get("price"))
                    .sum();

            // Validate buyer and seller existence
            if (!buyerService.existsById(buyerId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                     .body("Invalid buyer ID");
            }
            if (!sellerService.existsById(sellerId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                     .body("Invalid seller ID");
            }

            // Deduct the amount from the buyer's balance
            buyerService.deductBalance(buyerId, totalAmount);

            // Add the amount to the seller's balance
            sellerService.addBalance(sellerId, totalAmount);

            // Create a transaction record
            Transaction transaction = new Transaction();
            transaction.setBuyerId(buyerId);
            transaction.setSellerId(sellerId);
            transaction.setAmount(totalAmount);
            transaction.setStatus("COMPLETED");
            transaction.setDescription("Payment for " + cartItems.size() + " items");

            // Save the transaction
            transactionRepository.save(transaction);

            // Return the transaction details as the receipt
            return ResponseEntity.ok(transaction);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body("Invalid QR code data format");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Payment processing failed: " + e.getMessage());
        }
    }

    /**
     * Data class for structured QR code data.
     */
    private static class QRCodeData {
        private String sellerId;
        private List<Map<String, Object>> cartItems;

        // Getters and setters
        public String getSellerId() {
            return sellerId;
        }

        public void setSellerId(String sellerId) {
            this.sellerId = sellerId;
        }

        public List<Map<String, Object>> getCartItems() {
            return cartItems;
        }

        public void setCartItems(List<Map<String, Object>> cartItems) {
            this.cartItems = cartItems;
        }
    }
}
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

import jakarta.servlet.http.HttpSession;

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
            @RequestParam String password,
            HttpSession session) {

        Seller seller = sellerService.validateSeller(email, password);
        Map<String, Object> response = new HashMap<>();

        if (seller != null) {
            session.setAttribute("seller", seller);
            response.put("message", "Login successful");
            response.put("redirectUrl", "/sellerDashboard"); // Redirect to seller dashboard
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

import jakarta.servlet.http.HttpSession;

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
            @RequestParam String password,
            HttpSession session) {
    
        Buyer buyer = buyerService.validateBuyer(email, password);
        Map<String, Object> response = new HashMap<>();
    
        if (buyer != null) {
            session.setAttribute("buyer", buyer);
            response.put("message", "Login successful");
            response.put("redirectUrl", "/buyerDashboard"); // Redirect to buyer dashboard
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
                    buyer.setUsername(updatedBuyer.getUsername());
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
