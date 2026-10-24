package com.microservice.product.exception;

/** Erreur levée lorsqu'une opération est incompatible avec le statut actuel. */
public class InvalidProductStateException extends RuntimeException {
    public InvalidProductStateException(String message) {
        super(message);
    }
}
