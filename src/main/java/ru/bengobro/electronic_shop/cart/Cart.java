package ru.bengobro.electronic_shop.cart;

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

/** Корзина пользователя — корень агрегата с позициями {@link CartItem}. */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED, onConstructor_ = @PersistenceCreator)
@Table("cart")
public class Cart {

    @Id
    private Long id;
    private Long userId;

    @MappedCollection(idColumn = "cart_id")
    private Set<CartItem> items = new LinkedHashSet<>();

    public Cart(Long userId, Set<CartItem> items) {
        this.userId = userId;
        this.items = (items != null) ? new LinkedHashSet<>(items) : new LinkedHashSet<>();
    }
}
