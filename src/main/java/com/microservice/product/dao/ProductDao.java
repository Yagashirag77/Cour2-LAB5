package com.microservice.product.dao;

import com.microservice.product.entity.Product;
import com.microservice.product.entity.ProductStatus;

import java.util.List;

/**
 * Extension du DAO générique avec les requêtes propres au catalogue produit.
 */
public interface ProductDao extends IDao<Product> {
    boolean existsBySku(String sku, Long excludedProductId);

    boolean existsById(Long id);

    List<Product> search(String name, ProductStatus status);

    double calculateInventoryValue();
}
