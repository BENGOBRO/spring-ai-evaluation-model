package ru.bengobro.electronic_shop.cart;

import java.util.LinkedHashSet;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bengobro.electronic_shop.product.ProductRepository;
import ru.bengobro.electronic_shop.user.UserRepository;
import ru.bengobro.electronic_shop.web.BusinessException;
import ru.bengobro.electronic_shop.web.NotFoundException;

@Service
public class CartService {

    private final CartRepository carts;
    private final ProductRepository products;
    private final UserRepository users;

    public CartService(CartRepository carts, ProductRepository products, UserRepository users) {
        this.carts = carts;
        this.products = products;
        this.users = users;
    }

    /** Корзина пользователя; если ещё не создана — возвращается пустая (без сохранения). */
    public Cart getByUser(Long userId) {
        if (!users.existsById(userId)) {
            throw new NotFoundException("User " + userId + " not found");
        }
        return carts.findByUserId(userId).orElseGet(() -> new Cart(userId, new LinkedHashSet<>()));
    }

    /** Добавление товара в корзину: если товар уже есть — увеличивается количество. */
    @Transactional
    public Cart addItem(Long userId, Long productId, Integer quantity) {
        int qty = (quantity == null) ? 1 : quantity;
        if (qty <= 0) {
            throw new BusinessException("quantity must be positive");
        }
        if (!users.existsById(userId)) {
            throw new NotFoundException("User " + userId + " not found");
        }
        if (!products.existsById(productId)) {
            throw new NotFoundException("Product " + productId + " not found");
        }

        Cart cart = carts.findByUserId(userId)
                .orElseGet(() -> new Cart(userId, new LinkedHashSet<>()));

        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst();
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + qty);
        } else {
            cart.getItems().add(new CartItem(productId, qty));
        }
        return carts.save(cart);
    }
}
