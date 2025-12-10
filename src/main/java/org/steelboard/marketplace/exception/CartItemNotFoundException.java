package org.steelboard.marketplace.exception;

public class CartItemNotFoundException extends RuntimeException {

    public CartItemNotFoundException(Long id, String username) {
        super("Could not find cart item with id " + id + " for user " + username);
    }
}
