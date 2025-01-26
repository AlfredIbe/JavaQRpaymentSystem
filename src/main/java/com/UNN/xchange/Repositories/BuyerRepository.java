package com.UNN.xchange.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.UNN.xchange.Models.Buyer;

@Repository
public interface BuyerRepository extends JpaRepository<Buyer, Long> {
    <Optional>Buyer findByEmail(String email);
}