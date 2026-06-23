package ru.bengobro.electronic_shop.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Запрос на добавление товара в корзину. quantity опционально (по умолчанию 1). */
public record AddCartItemRequest(
        @NotNull Long userId,
        @NotNull Long productId,
        @Positive Integer quantity
) {
}
