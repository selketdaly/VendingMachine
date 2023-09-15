package org.daly.VendingMachine.controller;

public class ProductUnavailableException extends RuntimeException {
    public ProductUnavailableException(int productId) {
        super("Product with ID " + productId + " is out of stock. Please choose another option");
    }
}
