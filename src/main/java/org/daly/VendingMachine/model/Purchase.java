package org.daly.VendingMachine.model;

import lombok.Data;

import java.util.List;

@Data
public class Purchase {
    private String productName;
    private List<Coin> change;
}
