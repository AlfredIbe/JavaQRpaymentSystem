package com.UNN.xchange.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.UNN.xchange.Models.RevenueData;
import com.UNN.xchange.Models.SalesData;

@Repository
public interface SalesRepository extends JpaRepository<SalesData, Long> {

    @Query("SELECT new com.UNN.xchange.Models.SalesData(s.month, SUM(s.sales)) " +
           "FROM SalesData s WHERE s.seller.id = :sellerId GROUP BY s.month")
    List<SalesData> findSalesDataBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT new com.UNN.xchange.Models.RevenueData(s.month, SUM(s.revenue)) " +
           "FROM RevenueData s WHERE s.seller.id = :sellerId GROUP BY s.month")
    List<RevenueData> findRevenueDataBySellerId(@Param("sellerId") Long sellerId);

static List<SalesData> findBySellerId(Long sellerId) {
       // TODO Auto-generated method stub
       throw new UnsupportedOperationException("Unimplemented method 'findBySellerId'");
}
}