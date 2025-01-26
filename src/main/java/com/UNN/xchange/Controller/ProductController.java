package com.UNN.xchange.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.UNN.xchange.Models.Product;
import com.UNN.xchange.Repositories.ProductRepository;

@RestController
@RequestMapping("/sellers/{sellersID}/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // Define the directory where images will be saved
    private static final String UPLOAD_DIR = "uploads/";

    // Add a new product
    @PostMapping("/scan")
    public ResponseEntity<String> uploadProduct(
            @RequestParam("product-name") String name,
            @RequestParam("product-quantity") int quantity,
            @RequestParam("product-price") double price,
            @RequestParam("product-image") MultipartFile image) {

        try {
            // 1. Save the image file to the server
            String imagePath = saveImage(image);

            // 2. Create a Product object
            Product product = new Product();
            product.setName(name);
            product.setQuantity(quantity);
            product.setPrice(price);
            product.setimageurl(imagePath); // Save the image path in the database

            // 3. Save the product to the database
            productRepository.save(product);

            // 4. Return a success response
            return ResponseEntity.ok("Product uploaded successfully!");
        } catch (IOException e) {
            // Handle file upload errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image: " + e.getMessage());
        } catch (Exception e) {
            // Handle other errors (e.g., database errors)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload product: " + e.getMessage());
        }
    }

    // Get all products
    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    @GetMapping("/seller/{sellerId}")
public Optional<Product> getProductsBySeller(@PathVariable Long sellerId) {
    return productRepository.findBySeller_Id(sellerId);
}
    // Delete a product by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        try {
            productRepository.deleteById(id);
            return ResponseEntity.ok("Product deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete product: " + e.getMessage());
        }
    }

    // Helper method to save the image file
    private String saveImage(MultipartFile image) throws IOException {
        // Create the upload directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate a unique file name
        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();

        // Save the file to the server
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(image.getInputStream(), filePath);

        // Return the file path (or URL) to be stored in the database
        return UPLOAD_DIR + fileName;
    }
}