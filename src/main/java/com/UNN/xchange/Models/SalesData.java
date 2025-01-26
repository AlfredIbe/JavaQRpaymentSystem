package com.UNN.xchange.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class SalesData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String month; // e.g., "January", "February"
    private double sales; // Total sales for the month

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller seller; 

    // Constructors
    public SalesData() {}

    public SalesData(String month, double sales) {
        this.month = month;
        this.sales = sales;
    }

    public SalesData(String month, double sales, Seller seller) {
        this.month = month;
        this.sales = sales;
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

    public double getSales() {
        return sales;
    }

    public void setSales(double sales) {
        this.sales = sales;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }
}