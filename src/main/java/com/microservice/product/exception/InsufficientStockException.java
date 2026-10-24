package com.microservice.product.exception;

/** Erreur métier indiquant que la quantité demandée dépasse le stock. */
public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(int requested, int available) {
        super("Stock insuffisant : demandé=" + requested + ", disponible=" + available);
    }
}
