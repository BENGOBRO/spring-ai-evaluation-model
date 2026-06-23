package ru.bengobro.electronic_shop.order;

public record OrderStatusResponse(Long orderId, OrderStatus status) {

    public static OrderStatusResponse from(Order order) {
        return new OrderStatusResponse(order.getId(), order.getStatus());
    }
}
