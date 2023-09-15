package org.daly.VendingMachine.model;

public enum CoinType {
    FIVE_PENCE(5), TEN_PENCE(10), TWENTY_PENCE(20), FIFTY_PENCE(50), POUND(100);

    private final int value;

    CoinType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
