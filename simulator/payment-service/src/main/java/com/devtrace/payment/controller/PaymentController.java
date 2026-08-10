package com.devtrace.payment.controller;

import com.devtrace.payment.dto.PaymentRequest;
import com.devtrace.payment.dto.PaymentResponse;
import com.devtrace.payment.service.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public PaymentResponse processPayment(
            @RequestHeader(value = "X-Request-ID", required = false) String requestId,
            @RequestBody PaymentRequest request) {
        return paymentService.processPayment(request, requestId);
    }
}
