package com.devtrace.order.client;

import com.devtrace.order.dto.PaymentRequest;
import com.devtrace.order.dto.PaymentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PaymentClient {

    private final RestClient restClient;

    public PaymentClient(RestClient.Builder builder, @Value("${payment-service.url}") String paymentServiceUrl) {
        this.restClient = builder.baseUrl(paymentServiceUrl).build();
    }

    public PaymentResponse processPayment(PaymentRequest request, String requestId) {
        return restClient.post()
                .uri("/payments")
                .header("X-Request-ID", requestId)
                .body(request)
                .retrieve()
                .body(PaymentResponse.class);
    }
}
