package ru.bengobro.electronic_shop.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Запрос на создание заказа. quantity опционально (по умолчанию 1). */
public record CreateOrderRequest(
        @NotNull Long productId,
        @NotNull Long userId,
        @Positive Integer quantity
) {
}
