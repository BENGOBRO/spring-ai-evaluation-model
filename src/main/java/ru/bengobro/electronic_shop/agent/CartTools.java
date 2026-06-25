package ru.bengobro.electronic_shop.agent;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import ru.bengobro.electronic_shop.cart.CartResponse;
import ru.bengobro.electronic_shop.cart.CartService;

/** Инструменты агента над корзиной. */
@Component
public class CartTools {

    private final CartService carts;
    private final AgentInvocationRecorder recorder;

    public CartTools(CartService carts, AgentInvocationRecorder recorder) {
        this.carts = carts;
        this.recorder = recorder;
    }

    @Tool(description = "Добавить товар в корзину пользователя")
    public CartResponse addToCart(
            @ToolParam(description = "идентификатор пользователя") Long userId,
            @ToolParam(description = "идентификатор товара") Long productId,
            @ToolParam(required = false, description = "количество, по умолчанию 1") Integer quantity) {
        recorder.record("addToCart", ToolArgs.of("userId", userId, "productId", productId, "quantity", quantity));
        return CartResponse.from(carts.addItem(userId, productId, quantity));
    }

    @Tool(description = "Показать содержимое корзины пользователя")
    public CartResponse getCart(
            @ToolParam(description = "идентификатор пользователя") Long userId) {
        recorder.record("getCart", ToolArgs.of("userId", userId));
        return CartResponse.from(carts.getByUser(userId));
    }
}
