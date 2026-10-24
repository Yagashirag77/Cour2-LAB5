package com.microservice.product.exception;

/** Erreur métier utilisée lorsqu'aucun produit ne correspond à l'identifiant. */
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("Produit introuvable avec l'identifiant : " + id);
    }
}
