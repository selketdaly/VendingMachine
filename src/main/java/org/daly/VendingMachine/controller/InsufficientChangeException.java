package org.daly.VendingMachine.controller;

public class InsufficientChangeException extends RuntimeException {
    public InsufficientChangeException() {
        super("Cannot offer exact change. Please make another selection or provide exact change");
    }
}
