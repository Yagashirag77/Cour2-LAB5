package com.microservice.product.service;

import com.microservice.product.entity.Product;
import com.microservice.product.entity.ProductStatus;

import java.util.List;

/**
 * Façade métier du catalogue. Le programme principal dépend de ce contrat et
 * non de l'implémentation Hibernate.
 */
public interface ProductService {
    void addProduct(Product product);
    void updateProduct(Product product);
    void deleteProduct(Long id);
    Product getProductById(Long id);
    List<Product> getAllProducts();
    void addStock(Long productId, int quantity);
    void removeStock(Long productId, int quantity);
    void discontinueProduct(Long productId);
    List<Product> searchProducts(String name, ProductStatus status);
    double getInventoryValue();
}
