package ru.bengobro.electronic_shop.order;

import java.math.BigDecimal;

public record OrderItemResponse(Long productId, Integer quantity, BigDecimal priceAtPurchase) {

    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(item.getProductId(), item.getQuantity(), item.getPriceAtPurchase());
    }
}
