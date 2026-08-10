package com.devtrace.provider.controller;

import com.devtrace.provider.model.FailureMode;
import com.devtrace.provider.service.ProviderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/provider")
public class ProviderController {

    private final ProviderService providerService;

    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }

    @PostMapping("/payment")
    public String processPayment(
            @RequestHeader(value = "X-Request-ID", required = false) String requestId) {
        System.out.println("[Payment Provider] requestId=" + requestId);
        return providerService.processPayment();
    }

    @PostMapping("/simulation/failure")
    public String setFailureMode(@RequestParam FailureMode mode) {
        providerService.setFailureMode(mode);
        return "Payment provider failure mode: " + mode;
    }

    @PostMapping("/simulation/reset")
    public String resetFailureMode() {
        providerService.setFailureMode(FailureMode.NORMAL);
        return "Payment provider reset";
    }
}
