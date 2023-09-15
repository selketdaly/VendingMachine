package org.daly.VendingMachine.repository;

import org.daly.VendingMachine.model.Product;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface ProductRepository extends ListCrudRepository<Product, Integer> {
    List<Product> findAllByQuantityAvailableGreaterThan(int minQuantity);
}
