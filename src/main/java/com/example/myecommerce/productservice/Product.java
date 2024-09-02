package com.example.myecommerce.productservice;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private String description;

    private String image;

    private String category;

    private String brand;

    private int quantity;

    public Product(String name, double price, String description, String image, String category, String brand, int quantity) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.image = image;
        this.category = category;
        this.brand = brand;
        this.quantity = quantity;
    }
}
