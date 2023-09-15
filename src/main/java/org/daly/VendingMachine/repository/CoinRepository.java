package org.daly.VendingMachine.repository;

import org.daly.VendingMachine.model.Coin;
import org.daly.VendingMachine.model.CoinType;
import org.daly.VendingMachine.model.Product;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface CoinRepository extends ListCrudRepository<Coin, CoinType> {
}
