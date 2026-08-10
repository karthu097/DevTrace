package com.devtrace.inventory.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "inventory")
public class InventoryItem {

    @Id
    private Long productId;

    private String productName;

    private int quantity;

    protected InventoryItem() {
    }

    public InventoryItem(Long productId, String productName, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public boolean hasStock(int requestedQuantity) {
        return quantity >= requestedQuantity;
    }

    public void reserve(int requestedQuantity) {
        if (!hasStock(requestedQuantity)) {
            throw new IllegalStateException("Insufficient inventory");
        }
        quantity -= requestedQuantity;
    }
}
