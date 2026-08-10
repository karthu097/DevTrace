package com.devtrace.gateway.util;

import java.util.UUID;

public final class RequestIdUtil {
    private RequestIdUtil() {}

    public static String generate() {
        return "REQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
