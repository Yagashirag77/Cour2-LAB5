package com.microservice.product.exception;

/** Erreur métier levée lorsqu'une référence produit existe déjà. */
public class DuplicateSkuException extends RuntimeException {
    public DuplicateSkuException(String sku) {
        super("La référence produit existe déjà : " + sku);
    }
}
