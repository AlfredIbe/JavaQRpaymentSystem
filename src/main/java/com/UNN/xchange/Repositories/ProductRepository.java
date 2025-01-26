package com.UNN.xchange.Repositories;

import com.UNN.xchange.Models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Find a product by its name
    Product findByName(String name);

    // Find a product by its seller's ID (assuming a relationship with Seller entity)
    Optional<Product> findBySeller_Id(Long sellerId);
}