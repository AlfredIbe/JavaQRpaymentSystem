package com.UNN.xchange.Repositories;

import com.UNN.xchange.Models.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller>findByEmail(String email); // Find seller by email
}