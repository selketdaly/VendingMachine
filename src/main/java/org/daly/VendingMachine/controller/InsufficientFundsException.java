package org.daly.VendingMachine.controller;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(int payment, int price) {
        super("Payment of " + payment + "p is insufficient. Please pay " + price + "p");
    }
}
