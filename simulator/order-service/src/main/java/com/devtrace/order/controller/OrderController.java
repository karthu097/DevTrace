package com.devtrace.order.controller;

import com.devtrace.order.dto.OrderRequest;
import com.devtrace.order.dto.OrderResponse;
import com.devtrace.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {
    
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader(value = "X-Request-ID", required = false) String requestId,
            @RequestBody OrderRequest request) {

        if (requestId == null || requestId.isBlank()) {
            requestId = "REQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }

        System.out.println("[Order] Request " + requestId + " received by Order Service");

        return ResponseEntity.ok(orderService.createOrder(request, requestId));
    }
}
