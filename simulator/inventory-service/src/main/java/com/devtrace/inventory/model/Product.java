package com.devtrace.inventory.model;

public class Product {
    private final Long id;
    private final String name;
    private int quantity;

    public Product(Long id, String name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
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
