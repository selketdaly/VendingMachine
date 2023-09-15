package org.daly.VendingMachine.controller;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(int productId) {
        super("Could not find product with the ID " + productId);
    }
}
