package ru.bengobro.electronic_shop.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Заказ — корень агрегата. Позиции {@link OrderItem} сохраняются и читаются вместе с заказом;
 * связь между агрегатами (пользователь) хранится как идентификатор {@code userId}.
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED, onConstructor_ = @PersistenceCreator)
@Table("orders")
public class Order {

    @Id
    private Long id;
    private Long userId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private Instant createdAt;

    @MappedCollection(idColumn = "order_id")
    private Set<OrderItem> items = new LinkedHashSet<>();

    public Order(Long userId, OrderStatus status, BigDecimal totalAmount, Instant createdAt,
                 Set<OrderItem> items) {
        this.userId = userId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.items = (items != null) ? new LinkedHashSet<>(items) : new LinkedHashSet<>();
    }
}
