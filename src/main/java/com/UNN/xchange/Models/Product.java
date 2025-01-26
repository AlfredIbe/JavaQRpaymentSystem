package com.UNN.xchange.Models;

import org.hibernate.validator.constraints.URL;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.*;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Image URL is required")
    @URL(message = "Image URL must be a valid URL")
    private String imageurl;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be a positive number")
    private double price;

    @NotNull(message = "Quantity is required")
    @Positive(message = "QuantityQuantity must be a positive number")
    private int quantity;


    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller; // Assuming there's a Seller entity

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getimageurl () {
        return imageurl;
    }

    public void setimageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }
}
