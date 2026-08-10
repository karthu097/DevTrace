package com.devtrace.inventory.service;

import com.devtrace.inventory.entity.InventoryItem;
import com.devtrace.inventory.exception.ErrorCode;
import com.devtrace.inventory.exception.ServiceException;
import com.devtrace.inventory.model.FailureMode;
import com.devtrace.inventory.repository.InventoryRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private volatile FailureMode failureMode = FailureMode.NORMAL;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @PostConstruct
    public void seedDatabase() {
        if (inventoryRepository.count() == 0) {
            inventoryRepository.save(new InventoryItem(501L, "Mechanical Keyboard", 25));
            inventoryRepository.save(new InventoryItem(502L, "Wireless Mouse", 50));
            inventoryRepository.save(new InventoryItem(503L, "USB-C Hub", 10));
        }
    }

    public void setFailureMode(FailureMode failureMode) {
        this.failureMode = failureMode;
    }

    public FailureMode getFailureMode() {
        return failureMode;
    }

    public InventoryItem getProduct(Long productId) {
        return inventoryRepository.findById(productId)
                .orElseThrow(() -> new ServiceException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found: " + productId));
    }

    public synchronized InventoryItem reserve(Long productId, int quantity) {
        if (failureMode == FailureMode.SERVICE_UNAVAILABLE) {
            throw new ServiceException(ErrorCode.INVENTORY_UNAVAILABLE, "Inventory service is unavailable");
        }
        if (failureMode == FailureMode.SLOW) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        InventoryItem product = getProduct(productId);
        try {
            product.reserve(quantity);
        } catch (IllegalStateException e) {
            throw new ServiceException(ErrorCode.INSUFFICIENT_INVENTORY, "Insufficient inventory");
        }
        
        return inventoryRepository.save(product);
    }
}
