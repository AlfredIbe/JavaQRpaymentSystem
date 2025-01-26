package com.UNN.xchange.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.UNN.xchange.Models.RevenueData;
import com.UNN.xchange.Models.SalesData;
import com.UNN.xchange.Services.SellerService;

@RestController
@RequestMapping("/api/seller/chart")
public class ChartController {

    @Autowired
    private SellerService sellerService; // Corrected variable name

    /**
     * Fetch sales data for a specific seller.
     *
     * @param sellerId The ID of the seller.
     * @return A list of SalesData objects.
     */
    @GetMapping("/sellers/{sellerId}/sales")
    public List<SalesData> getSalesData(@PathVariable Long sellerId) {
        return sellerService.getSalesData(sellerId);
    }

    /**
     * Fetch revenue data for a specific seller.
     *
     * @param sellerId The ID of the seller.
     * @return A list of RevenueData objects.
     */
    @GetMapping("/sellers/{sellerId}/revenue")
    public List<RevenueData> getRevenueData(@PathVariable Long sellerId) {
        return sellerService.getRevenueData(sellerId);
    }
}
