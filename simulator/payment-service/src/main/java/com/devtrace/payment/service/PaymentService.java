package com.devtrace.payment.service;

import com.devtrace.payment.client.PaymentProviderClient;
import com.devtrace.payment.dto.PaymentRequest;
import com.devtrace.payment.dto.PaymentResponse;
import com.devtrace.payment.entity.Payment;
import com.devtrace.payment.exception.ErrorCode;
import com.devtrace.payment.exception.ServiceException;
import com.devtrace.payment.logging.StructuredLogger;
import com.devtrace.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
public class PaymentService {

    private final PaymentProviderClient providerClient;
    private final PaymentRepository paymentRepository;
    private final StructuredLogger logger;

    public PaymentService(
            PaymentProviderClient providerClient,
            PaymentRepository paymentRepository,
            StructuredLogger logger) {
        this.providerClient = providerClient;
        this.paymentRepository = paymentRepository;
        this.logger = logger;
    }

    public PaymentResponse processPayment(PaymentRequest request, String requestId) {
        logger.info("PAYMENT_STARTED", requestId, "Starting payment processing for order " + request.orderId());

        Payment payment = new Payment(request.orderId(), request.amount(), request.paymentMethod(), "PENDING");
        paymentRepository.save(payment);

        logger.info("DEPENDENCY_CALL_STARTED", requestId, "Calling Payment Provider");
        String providerResponse;
        try {
            providerResponse = providerClient.processPayment(requestId);
            logger.info("DEPENDENCY_CALL_COMPLETED", requestId, "Payment Provider call succeeded");
        } catch (RestClientException e) {
            logger.error("DEPENDENCY_TIMEOUT", requestId, "Payment Provider timed out: " + e.getMessage());
            payment.setStatus("FAILED");
            paymentRepository.save(payment);
            throw new ServiceException(ErrorCode.PAYMENT_TIMEOUT, "Payment provider did not respond within 3 seconds");
        } catch (Exception e) {
            logger.error("DEPENDENCY_UNAVAILABLE", requestId, "Payment Provider error: " + e.getMessage());
            payment.setStatus("FAILED");
            paymentRepository.save(payment);
            throw new ServiceException(ErrorCode.PAYMENT_FAILED, "Payment provider error: " + e.getMessage());
        }

        if ("PAYMENT_APPROVED".equals(providerResponse)) {
            payment.setStatus("APPROVED");
            logger.info("PAYMENT_APPROVED", requestId, "Payment successfully approved");
        } else {
            payment.setStatus("FAILED");
            logger.error("PAYMENT_FAILED", requestId, "Payment declined by provider: " + providerResponse);
            paymentRepository.save(payment);
            throw new ServiceException(ErrorCode.PAYMENT_FAILED, "Payment declined");
        }
        
        paymentRepository.save(payment);

        return new PaymentResponse(request.orderId(), providerResponse);
    }
}
