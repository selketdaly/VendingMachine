package org.daly.VendingMachine.service;

import lombok.AllArgsConstructor;
import org.daly.VendingMachine.controller.InsufficientChangeException;
import org.daly.VendingMachine.model.Coin;
import org.daly.VendingMachine.model.CoinType;
import org.daly.VendingMachine.repository.CoinRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class CoinService {
    private CoinRepository coinRepository;

    private static int calculateChangeForDenomination(int changeRequired, Map<CoinType, Integer> availableChange, CoinType denomination, List<Coin> change) {
        int coinsRequired = changeRequired / denomination.getValue();
        int coinsAvailable = availableChange.getOrDefault(denomination, 0);
        change.add(new Coin(denomination, Math.min(coinsRequired, coinsAvailable)));

        changeRequired = changeRequired - (denomination.getValue() * Math.min(coinsRequired, coinsAvailable));
        return changeRequired;
    }

    public List<Coin> calculateChange(int payment, int purchasePrice) {
        List<Coin> change = new ArrayList<>();
        List<Coin> avaialableCoins = coinRepository.findAll();
        Map<CoinType, Integer> availableChange = mapAvailableCoins(avaialableCoins);
        if (payment != purchasePrice) {
            int changeRequired = payment - purchasePrice;

            changeRequired = calculateChangeForDenomination(changeRequired, availableChange, CoinType.POUND, change);
            changeRequired = calculateChangeForDenomination(changeRequired, availableChange, CoinType.FIFTY_PENCE, change);
            changeRequired = calculateChangeForDenomination(changeRequired, availableChange, CoinType.TWENTY_PENCE, change);
            changeRequired = calculateChangeForDenomination(changeRequired, availableChange, CoinType.TEN_PENCE, change);
            changeRequired = calculateChangeForDenomination(changeRequired, availableChange, CoinType.FIVE_PENCE, change);

            if (changeRequired != 0) {
                throw new InsufficientChangeException();
            }
        }
        return change;
    }

    private Map<CoinType, Integer> mapAvailableCoins(List<Coin> availableCoins) {
        return availableCoins.stream().collect(Collectors.toMap(Coin::getCoinType, Coin::getQuantity));
    }

    public void makePayment(List<Coin> payment, List<Coin> change) {
        Map<CoinType, Integer> availableCoins = mapAvailableCoins(coinRepository.findAll());
        Map<CoinType, Integer> paymentCoins = mapAvailableCoins(payment);
        Map<CoinType, Integer> changeCoins = mapAvailableCoins(change);

        Map<CoinType, Integer> updatedCoins = Stream.of(availableCoins, paymentCoins).flatMap(map -> map.entrySet().stream()).collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingInt(Map.Entry::getValue)));

        for(Map.Entry<CoinType, Integer> updatedCoin: updatedCoins.entrySet()) {
            updatedCoin.setValue(updatedCoin.getValue() - changeCoins.get(updatedCoin.getKey()));
        }

        List<Coin> coinsToSave = updatedCoins.entrySet().stream().map(coin -> new Coin(coin.getKey(), coin.getValue())).toList();

        coinRepository.saveAll(coinsToSave);


    }
}
