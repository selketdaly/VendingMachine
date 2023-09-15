package org.daly.VendingMachine.controller;

import lombok.AllArgsConstructor;
import org.daly.VendingMachine.model.Coin;
import org.daly.VendingMachine.model.Product;
import org.daly.VendingMachine.model.Purchase;
import org.daly.VendingMachine.model.Request;
import org.daly.VendingMachine.repository.ProductRepository;
import org.daly.VendingMachine.service.CoinService;
import org.daly.VendingMachine.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/purchase")
@AllArgsConstructor
public class PurchaseController {
    private ProductService productService;
    private CoinService coinService;

    @PostMapping
    public Purchase purchaseProduct(@RequestBody Request purchaseRequest) {
        Product product = productService.getProductById(purchaseRequest.getProductId());
        Purchase purchase = new Purchase();
        if(product.getQuantityAvailable() > 0) {
            int provided = purchaseRequest.getMoneyProvided();
            if(provided < product.getPrice()) {
                throw new InsufficientFundsException(provided, product.getPrice());
            }

            purchase.setProductName(product.getName());
            List<Coin> change = coinService.calculateChange(provided, product.getPrice());
            purchase.setChange(change);

            coinService.makePayment(purchaseRequest.getCoins(), change);
            productService.sellProduct(product.getProductId());
        } else {
            throw new ProductUnavailableException(purchaseRequest.getProductId());
        }
        return purchase;
    }

}
