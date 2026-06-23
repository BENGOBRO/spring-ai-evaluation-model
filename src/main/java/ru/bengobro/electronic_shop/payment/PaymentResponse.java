package ru.bengobro.electronic_shop.payment;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        Long id,
        Long orderId,
        BigDecimal amount,
        PaymentStatus status,
        String method,
        Instant paidAt
) {
    public static PaymentResponse from(Payment p) {
        return new PaymentResponse(p.getId(), p.getOrderId(), p.getAmount(),
                p.getStatus(), p.getMethod(), p.getPaidAt());
    }
}
