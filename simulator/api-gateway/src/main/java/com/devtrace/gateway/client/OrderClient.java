package com.devtrace.gateway.client;

import com.devtrace.gateway.dto.CreateOrderRequest;
import com.devtrace.gateway.dto.OrderResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OrderClient {
    private final RestClient restClient;

    public OrderClient(RestClient.Builder builder, @Value("${order-service.url}") String orderServiceUrl) {
        this.restClient = builder.baseUrl(orderServiceUrl).build();
    }

    public OrderResponse createOrder(CreateOrderRequest request, String requestId) {
        return restClient.post()
                .uri("/orders")
                .header("X-Request-ID", requestId)
                .body(request)
                .retrieve()
                .body(OrderResponse.class);
    }
}
