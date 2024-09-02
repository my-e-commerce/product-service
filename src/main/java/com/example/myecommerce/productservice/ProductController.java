package com.example.myecommerce.productservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private static final Logger logger= LoggerFactory.getLogger(ProductController.class);

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        logger.info("GET /product - retrieving all products");
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        logger.info("GET /product - retrieving product with id {}", id);
        return productService.findById(id)
                .map(product -> {
                    logger.info("retrieved product with id {}", id);
                    return ResponseEntity.ok(product);})
                .orElseGet(()-> {
                    logger.warn("no product found with id {}", id);
                    return ResponseEntity.notFound().build();});
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductRequest productRequest, UriComponentsBuilder uriBuilder) {
        logger.info("POST /product - adding product {}", productRequest.name());
        Product product = productService.addProduct(productRequest);
        URI location = uriBuilder.path("/products/{id}").buildAndExpand(product.getId()).toUri();
        logger.info("URI created with id {}", product.getId());
        return ResponseEntity.created(location).body(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductRequest productRequest) {
        logger.info("PUT /product - updating product with id {}", id);
        return productService.updateProduct(id,productRequest)
                .map(product -> {
                    logger.info("updated product with id {}", id);
                    return ResponseEntity.ok(product);
                })
                .orElseGet(()-> {
                    logger.warn("no product found with id {}", id);
                    return ResponseEntity.notFound().build();});
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        logger.info("DELETE /product - deleting product with id {}", id);
        if (productService.deleteProduct(id)) {
            logger.info("deleted product with id {}", id);
            return ResponseEntity.noContent().build();
        } else
            logger.warn("no product found with id {}", id);
        return ResponseEntity.notFound().build();
    }
}
