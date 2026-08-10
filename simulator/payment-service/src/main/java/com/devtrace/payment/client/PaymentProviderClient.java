package com.devtrace.payment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PaymentProviderClient {

    private final RestClient restClient;

    public PaymentProviderClient(RestClient.Builder builder, @Value("${payment-provider.url}") String providerUrl) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(1000);
        requestFactory.setReadTimeout(3000);

        this.restClient = builder
                .baseUrl(providerUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public String processPayment(String requestId) {
        return restClient.post()
                .uri("/provider/payment")
                .header("X-Request-ID", requestId)
                .retrieve()
                .body(String.class);
    }
}
