package com.UNN.xchange.Repositories;

import com.UNN.xchange.Models.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {
    <Optional> Orders findByBuyerId(Long id);
}