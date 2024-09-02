package com.example.myecommerce.productservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @CircuitBreaker(name = "productServiceCircuitBreaker", fallbackMethod = "fallbackGetAllProducts")
    public List<Product> getAllProducts() {
        logger.info("Fetching all products");
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            logger.info("No products found");
        }
        else
            logger.info("Found {} products", products.size());
        return products;
    }

    @CircuitBreaker(name = "productServiceCircuitBreaker", fallbackMethod = "fallbackFindById")
    public Optional<Product> findById(Long id) {
        logger.info("Fetching product by id: {}", id);
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            logger.info("No product found with id: {}", id);
        }
        else
            logger.info("Found product with id: {}", id);
        return product;
    }

    @CircuitBreaker(name = "productServiceCircuitBreaker", fallbackMethod = "fallbackAddProduct")
    public Product addProduct(ProductRequest productRequest) {
        logger.info("Adding product: {}", productRequest.name());
        Product product = new Product(productRequest.name(),
                productRequest.price(),
                productRequest.description(),
                productRequest.image(),
                productRequest.category(),
                productRequest.brand(),
                productRequest.quantity());
        logger.info("Added product: {}", product.getId());
        return productRepository.save(product);
    }

    @CircuitBreaker(name = "productServiceCircuitBreaker", fallbackMethod = "fallbackUpdateProduct")
    public Optional<Product> updateProduct(Long id, ProductRequest productRequest) {
        logger.info("Updating product with id: {}", id);
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            product.setName(productRequest.name());
            product.setPrice(productRequest.price());
            product.setDescription(productRequest.description());
            product.setImage(productRequest.image());
            product.setCategory(productRequest.category());
            product.setBrand(productRequest.brand());
            product.setQuantity(productRequest.quantity());
            logger.info("Updated product: {}", product.getId());
            return Optional.of(productRepository.save(product));
        }
        logger.info("No product found with id: {}", id);
        return Optional.empty();
    }

    @CircuitBreaker(name = "productServiceCircuitBreaker", fallbackMethod = "fallbackDeleteProduct")
    public boolean deleteProduct(Long id) {
        logger.info("Deleting product with id: {}", id);
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            productRepository.delete(productOptional.get());
            logger.info("Deleted product: {}", id);
            return true;
        }
        logger.info("No product found with id: {}", id);
        return false;
    }
    // Fallback methods
    public List<Product> fallbackGetAllProducts(Throwable throwable) {
        logger.error("Error retrieving all products: {}", throwable.getMessage());
        return List.of(); // Return an empty list or some default response
    }

    public Optional<Product> fallbackFindById(Long id, Throwable throwable) {
        logger.error("Error retrieving product by ID {}: {}", id, throwable.getMessage());
        return Optional.empty(); // Return an empty Optional
    }

    public Product fallbackAddProduct(ProductRequest productRequest, Throwable throwable) {
        logger.error("Error adding product: {}", throwable.getMessage());
        return new Product(); // Return a default or dummy product
    }

    public Optional<Product> fallbackUpdateProduct(Long id, ProductRequest productRequest, Throwable throwable) {
        logger.error("Error updating product with ID {}: {}", id, throwable.getMessage());
        return Optional.empty(); // Return an empty Optional
    }

    public boolean fallbackDeleteProduct(Long id, Throwable throwable) {
        logger.error("Error deleting product with ID {}: {}", id, throwable.getMessage());
        return false; // Indicate that the delete operation failed
    }

}
