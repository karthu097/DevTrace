package com.devtrace.gateway.controller;

import com.devtrace.gateway.client.OrderClient;
import com.devtrace.gateway.dto.CreateOrderRequest;
import com.devtrace.gateway.dto.OrderResponse;
import com.devtrace.gateway.util.RequestIdUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class GatewayController {

    private final OrderClient orderClient;

    public GatewayController(OrderClient orderClient) {
        this.orderClient = orderClient;
    }

    @PostMapping("/orders")
    public OrderResponse createOrder(
            @RequestHeader(value = "X-Request-ID", required = false) String requestId,
            @RequestBody CreateOrderRequest request) {

        if (requestId == null || requestId.isBlank()) {
            requestId = RequestIdUtil.generate();
        }

        System.out.println("[Gateway] Request " + requestId + " received");

        return orderClient.createOrder(request, requestId);
    }
}
