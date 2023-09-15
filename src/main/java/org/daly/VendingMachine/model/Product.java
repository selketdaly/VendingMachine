package org.daly.VendingMachine.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
public class Product {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int productId;

    private String name;

    private int price;

    @Size(max = 5)
    private int quantityAvailable;

    public void updateQuantityBy(int quantityAdjustment) {
        quantityAvailable += quantityAdjustment;
    }
}
