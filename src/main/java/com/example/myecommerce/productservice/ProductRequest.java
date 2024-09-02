package com.example.myecommerce.productservice;

public record ProductRequest(String name, double price, String description, String image, String category, String brand, int quantity) {
}
