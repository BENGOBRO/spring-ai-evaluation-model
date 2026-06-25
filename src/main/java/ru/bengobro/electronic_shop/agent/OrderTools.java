package ru.bengobro.electronic_shop.agent;

import java.util.List;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import ru.bengobro.electronic_shop.order.OrderResponse;
import ru.bengobro.electronic_shop.order.OrderService;
import ru.bengobro.electronic_shop.order.OrderStatusResponse;

/** Инструменты агента над заказами. */
@Component
public class OrderTools {

    private final OrderService orders;
    private final AgentInvocationRecorder recorder;

    public OrderTools(OrderService orders, AgentInvocationRecorder recorder) {
        this.orders = orders;
        this.recorder = recorder;
    }

    @Tool(description = "Создать заказ на товар для пользователя")
    public OrderResponse createOrder(
            @ToolParam(description = "идентификатор товара") Long productId,
            @ToolParam(description = "идентификатор пользователя") Long userId,
            @ToolParam(required = false, description = "количество, по умолчанию 1") Integer quantity) {
        recorder.record("createOrder", ToolArgs.of("productId", productId, "userId", userId, "quantity", quantity));
        return OrderResponse.from(orders.create(productId, userId, quantity));
    }

    @Tool(description = "Узнать статус заказа по идентификатору")
    public OrderStatusResponse getOrderStatus(
            @ToolParam(description = "идентификатор заказа") Long orderId) {
        recorder.record("getOrderStatus", ToolArgs.of("orderId", orderId));
        return OrderStatusResponse.from(orders.getById(orderId));
    }

    @Tool(description = "Отменить заказ по идентификатору")
    public OrderResponse cancelOrder(
            @ToolParam(description = "идентификатор заказа") Long orderId) {
        recorder.record("cancelOrder", ToolArgs.of("orderId", orderId));
        return OrderResponse.from(orders.cancel(orderId));
    }

    @Tool(description = "Получить список заказов пользователя")
    public List<OrderResponse> getUserOrders(
            @ToolParam(description = "идентификатор пользователя") Long userId) {
        recorder.record("getUserOrders", ToolArgs.of("userId", userId));
        return orders.getByUser(userId).stream().map(OrderResponse::from).toList();
    }
}
