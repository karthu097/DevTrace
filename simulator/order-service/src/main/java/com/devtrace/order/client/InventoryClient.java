package com.devtrace.order.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InventoryClient {

    private final RestClient restClient;

    public InventoryClient(RestClient.Builder builder, @Value("${inventory-service.url}") String inventoryServiceUrl) {
        this.restClient = builder.baseUrl(inventoryServiceUrl).build();
    }

    public void reserve(Long productId, int quantity, String requestId) {
        restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/inventory/{productId}/reserve")
                        .queryParam("quantity", quantity)
                        .build(productId)
                )
                .header("X-Request-ID", requestId)
                .retrieve()
                .toBodilessEntity();
    }
}
