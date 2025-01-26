package com.UNN.xchange.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class RevenueData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String month; // e.g., "January", "February"
    private double revenue; // Total revenue for the month

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller seller; 

    // Constructors
    public RevenueData() {}

    public RevenueData(String month, double revenue) {
        this.month = month;
        this.revenue = revenue;
    }

    public RevenueData(String month, double revenue, Seller seller) {
        this.month = month;
        this.revenue = revenue;
        this.seller = seller;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }
}