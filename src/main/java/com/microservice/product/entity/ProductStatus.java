package com.microservice.product.entity;

/**
 * États métier possibles d'un produit. AVAILABLE et OUT_OF_STOCK dépendent de
 * la quantité, tandis que DISCONTINUED représente une décision définitive.
 */
public enum ProductStatus {
    AVAILABLE,
    OUT_OF_STOCK,
    DISCONTINUED
}
