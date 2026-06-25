package ru.bengobro.electronic_shop.payment;

import jakarta.validation.constraints.NotNull;

/** Запрос на оплату заказа. method опционально (по умолчанию CARD). */
public record CreatePaymentRequest(
        @NotNull Long orderId,
        String method
) {
}
