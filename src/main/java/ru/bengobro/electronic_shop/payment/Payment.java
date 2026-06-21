package ru.bengobro.electronic_shop.payment;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

/** Оплата заказа. Ссылка на заказ — по идентификатору {@code orderId}. */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED, onConstructor_ = @PersistenceCreator)
@Table("payment")
public class Payment {

    @Id
    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String method;
    private Instant paidAt;

    public Payment(Long orderId, BigDecimal amount, PaymentStatus status, String method, Instant paidAt) {
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
        this.method = method;
        this.paidAt = paidAt;
    }
}
