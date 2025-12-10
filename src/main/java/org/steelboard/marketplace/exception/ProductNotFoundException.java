package org.steelboard.marketplace.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String name) {
        super("Product not found with name: " + name);
    }

    public ProductNotFoundException(Long id) {
        super("Product not found with id: " + id);
    }
}
