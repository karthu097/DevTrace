package com.devtrace.order.service;

import com.devtrace.order.client.InventoryClient;
import com.devtrace.order.client.PaymentClient;
import com.devtrace.order.dto.OrderRequest;
import com.devtrace.order.dto.OrderResponse;
import com.devtrace.order.dto.PaymentRequest;
import com.devtrace.order.dto.PaymentResponse;
import com.devtrace.order.entity.Order;
import com.devtrace.order.exception.ErrorCode;
import com.devtrace.order.exception.ServiceException;
import com.devtrace.order.logging.StructuredLogger;
import com.devtrace.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class OrderService {

    private final InventoryClient inventoryClient;
    private final PaymentClient paymentClient;
    private final OrderRepository orderRepository;
    private final StructuredLogger logger;

    public OrderService(
            InventoryClient inventoryClient,
            PaymentClient paymentClient,
            OrderRepository orderRepository,
            StructuredLogger logger) {
        this.inventoryClient = inventoryClient;
        this.paymentClient = paymentClient;
        this.orderRepository = orderRepository;
        this.logger = logger;
    }

    public OrderResponse createOrder(OrderRequest request, String requestId) {
        logger.info("ORDER_RECEIVED", requestId, "Order request received");

        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        Order order = new Order(
                orderId,
                request.getUserId(),
                request.getProductId(),
                request.getQuantity(),
                "CREATED"
        );
        orderRepository.save(order);

        logger.info("INVENTORY_RESERVATION_STARTED", requestId, "Starting inventory reservation");
        try {
            inventoryClient.reserve(request.getProductId(), request.getQuantity(), requestId);
            logger.info("INVENTORY_RESERVED", requestId, "Inventory successfully reserved");
        } catch (Exception e) {
            logger.error("INVENTORY_RESERVATION_FAILED", requestId, "Inventory reservation failed: " + e.getMessage());
            order.setStatus("FAILED");
            orderRepository.save(order);
            throw new ServiceException(ErrorCode.INVENTORY_UNAVAILABLE, "Failed to reserve inventory");
        }

        logger.info("PAYMENT_STARTED", requestId, "Starting payment");
        PaymentRequest paymentRequest = new PaymentRequest(orderId, BigDecimal.valueOf(1499), "CARD");
        
        PaymentResponse paymentResponse;
        try {
            paymentResponse = paymentClient.processPayment(paymentRequest, requestId);
        } catch (RestClientException e) {
            logger.error("PAYMENT_TIMEOUT", requestId, "Payment timed out: " + e.getMessage());
            order.setStatus("FAILED");
            orderRepository.save(order);
            throw new ServiceException(ErrorCode.PAYMENT_TIMEOUT, "Payment provider did not respond within timeout");
        } catch (Exception e) {
            logger.error("PAYMENT_FAILED", requestId, "Payment failed: " + e.getMessage());
            order.setStatus("FAILED");
            orderRepository.save(order);
            throw new ServiceException(ErrorCode.PAYMENT_FAILED, "Payment failed: " + e.getMessage());
        }

        if (!"PAYMENT_APPROVED".equals(paymentResponse.getStatus())) {
            logger.error("PAYMENT_FAILED", requestId, "Payment not approved: " + paymentResponse.getStatus());
            order.setStatus("FAILED");
            orderRepository.save(order);
            throw new ServiceException(ErrorCode.PAYMENT_FAILED, "Payment not approved");
        }

        logger.info("ORDER_CONFIRMED", requestId, "Order successfully confirmed");
        order.setStatus("CONFIRMED");
        orderRepository.save(order);

        return new OrderResponse(orderId, "CONFIRMED");
    }
}
