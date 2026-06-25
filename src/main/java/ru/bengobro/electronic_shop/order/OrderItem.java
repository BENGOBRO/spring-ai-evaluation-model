package ru.bengobro.electronic_shop.order;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Позиция заказа — часть агрегата {@link Order}.
 * Обратная ссылка на заказ (order_id) задаётся через {@code @MappedCollection} в {@link Order}.
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED, onConstructor_ = @PersistenceCreator)
@Table("order_item")
public class OrderItem {

    @Id
    private Long id;
    private Long productId;
    private Integer quantity;
    private BigDecimal priceAtPurchase;

    public OrderItem(Long productId, Integer quantity, BigDecimal priceAtPurchase) {
        this.productId = productId;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
    }
}
