package com.devtrace.provider.service;

import com.devtrace.provider.model.FailureMode;
import org.springframework.stereotype.Service;

@Service
public class ProviderService {

    private volatile FailureMode failureMode = FailureMode.NORMAL;

    public String processPayment() {
        switch (failureMode) {
            case TIMEOUT:
                sleep(5000);
                return "PAYMENT_APPROVED";
            case SLOW:
                sleep(3000);
                return "PAYMENT_APPROVED";
            case HTTP_500:
                throw new IllegalStateException("Payment provider internal error");
            case CONNECTION_FAILURE:
                throw new RuntimeException("Payment provider connection failure");
            case NORMAL:
            default:
                return "PAYMENT_APPROVED";
        }
    }

    public void setFailureMode(FailureMode failureMode) {
        this.failureMode = failureMode;
    }

    public FailureMode getFailureMode() {
        return failureMode;
    }

    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Provider interrupted", e);
        }
    }
}
