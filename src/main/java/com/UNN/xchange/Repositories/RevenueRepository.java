package com.UNN.xchange.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.UNN.xchange.Models.RevenueData;

public interface RevenueRepository extends JpaRepository<RevenueData, Long> {
    static List<RevenueData> findBySellerId(Long sellerId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findBySellerId'");
    }
}