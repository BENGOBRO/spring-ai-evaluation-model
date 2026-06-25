package ru.bengobro.electronic_shop.cart;

import java.util.List;

public record CartResponse(Long id, Long userId, List<CartItemResponse> items) {

    public record CartItemResponse(Long productId, Integer quantity) {
        static CartItemResponse from(CartItem item) {
            return new CartItemResponse(item.getProductId(), item.getQuantity());
        }
    }

    public static CartResponse from(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(CartItemResponse::from)
                .toList();
        return new CartResponse(cart.getId(), cart.getUserId(), items);
    }
}
