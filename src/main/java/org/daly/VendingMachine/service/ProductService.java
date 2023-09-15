package org.daly.VendingMachine.service;

import lombok.AllArgsConstructor;
import org.daly.VendingMachine.controller.ProductNotFoundException;
import org.daly.VendingMachine.model.Product;
import org.daly.VendingMachine.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {
    private ProductRepository productRepository;

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public List<Product> getAvailableProducts() {
        return productRepository.findAllByQuantityAvailableGreaterThan(0);
    }

    public Product getProductById(int productId) {
        return productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
    }

    public boolean checkProductAvailability(int productId) {
        return getProductById(productId).getQuantityAvailable() > 0;
    }

    public void sellProduct(int productId) {
        Product product = getProductById(productId);
        product.updateQuantityBy(-1);
        productRepository.save(product);
    }

}
