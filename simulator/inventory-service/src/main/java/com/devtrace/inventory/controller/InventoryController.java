package com.devtrace.inventory.controller;

import com.devtrace.inventory.entity.InventoryItem;
import com.devtrace.inventory.logging.StructuredLogger;
import com.devtrace.inventory.model.FailureMode;
import com.devtrace.inventory.service.InventoryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final StructuredLogger logger;

    public InventoryController(InventoryService inventoryService, StructuredLogger logger) {
        this.inventoryService = inventoryService;
        this.logger = logger;
    }

    @GetMapping("/{productId}")
    public InventoryItem getInventory(
            @RequestHeader(value = "X-Request-ID", required = false) String requestId,
            @PathVariable Long productId) {

        logger.info("INVENTORY_LOOKUP_STARTED", requestId, "Looking up product " + productId);
        return inventoryService.getProduct(productId);
    }

    @PostMapping("/{productId}/reserve")
    public InventoryItem reserveInventory(
            @RequestHeader(value = "X-Request-ID", required = false) String requestId,
            @PathVariable Long productId,
            @RequestParam int quantity) {

        logger.info("INVENTORY_RESERVATION_STARTED", requestId, "Reserving " + quantity + " of product " + productId);
        try {
            InventoryItem result = inventoryService.reserve(productId, quantity);
            logger.info("INVENTORY_RESERVED", requestId, "Successfully reserved inventory");
            return result;
        } catch (Exception e) {
            logger.error("INVENTORY_RESERVATION_FAILED", requestId, "Failed to reserve inventory: " + e.getMessage());
            throw e;
        }
    }

    @PostMapping("/simulation/failure")
    public String setFailureMode(@RequestParam FailureMode mode) {
        inventoryService.setFailureMode(mode);
        return "Inventory failure mode set to " + mode;
    }

    @PostMapping("/simulation/reset")
    public String resetFailure() {
        inventoryService.setFailureMode(FailureMode.NORMAL);
        return "Inventory failure mode reset";
    }
}
