package ru.bengobro.electronic_shop.payment;

import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bengobro.electronic_shop.order.Order;
import ru.bengobro.electronic_shop.order.OrderRepository;
import ru.bengobro.electronic_shop.order.OrderStatus;
import ru.bengobro.electronic_shop.web.BusinessException;
import ru.bengobro.electronic_shop.web.NotFoundException;

@Service
public class PaymentService {

    private final PaymentRepository payments;
    private final OrderRepository orders;

    public PaymentService(PaymentRepository payments, OrderRepository orders) {
        this.payments = payments;
        this.orders = orders;
    }

    /** Оплата заказа: фиксируется платёж, заказ переводится в статус PAID. */
    @Transactional
    public Payment pay(Long orderId, String method) {
        Order order = orders.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order " + orderId + " not found"));
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("Cannot pay a cancelled order " + orderId);
        }
        if (order.getStatus() == OrderStatus.PAID) {
            throw new BusinessException("Order " + orderId + " is already paid");
        }

        Payment payment = new Payment(orderId, order.getTotalAmount(), PaymentStatus.SUCCESS,
                (method == null || method.isBlank()) ? "CARD" : method, Instant.now());
        Payment saved = payments.save(payment);

        order.setStatus(OrderStatus.PAID);
        orders.save(order);
        return saved;
    }
}
