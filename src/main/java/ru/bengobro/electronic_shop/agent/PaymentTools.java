package ru.bengobro.electronic_shop.agent;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import ru.bengobro.electronic_shop.payment.PaymentResponse;
import ru.bengobro.electronic_shop.payment.PaymentService;

/** Инструменты агента над оплатами. */
@Component
public class PaymentTools {

    private final PaymentService payments;
    private final AgentInvocationRecorder recorder;

    public PaymentTools(PaymentService payments, AgentInvocationRecorder recorder) {
        this.payments = payments;
        this.recorder = recorder;
    }

    @Tool(description = "Оплатить заказ по идентификатору")
    public PaymentResponse createPayment(
            @ToolParam(description = "идентификатор заказа") Long orderId,
            @ToolParam(required = false, description = "способ оплаты, по умолчанию CARD") String method) {
        recorder.record("createPayment", ToolArgs.of("orderId", orderId, "method", method));
        return PaymentResponse.from(payments.pay(orderId, method));
    }
}
