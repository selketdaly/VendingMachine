package org.daly.VendingMachine.model;

import lombok.Data;

import java.util.List;

@Data
public class Request {
    private int productId;
    private List<Coin> coins;
    public int getMoneyProvided() {
        return coins.stream().map(coin -> {
            return coin.getQuantity() * coin.getCoinType().getValue();
        }).reduce(0, Integer::sum);
    }
}
