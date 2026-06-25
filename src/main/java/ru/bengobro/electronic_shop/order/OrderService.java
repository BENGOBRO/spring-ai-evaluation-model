package ru.bengobro.electronic_shop.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bengobro.electronic_shop.product.Product;
import ru.bengobro.electronic_shop.product.ProductRepository;
import ru.bengobro.electronic_shop.user.UserRepository;
import ru.bengobro.electronic_shop.web.BusinessException;
import ru.bengobro.electronic_shop.web.NotFoundException;

@Service
public class OrderService {

    private final OrderRepository orders;
    private final ProductRepository products;
    private final UserRepository users;

    public OrderService(OrderRepository orders, ProductRepository products, UserRepository users) {
        this.orders = orders;
        this.products = products;
        this.users = users;
    }

    public Order getById(Long id) {
        return orders.findById(id)
                .orElseThrow(() -> new NotFoundException("Order " + id + " not found"));
    }

    public List<Order> getByUser(Long userId) {
        return orders.findByUserId(userId);
    }

    /** Создание заказа: проверка пользователя/товара/склада, расчёт суммы, списание остатка. */
    @Transactional
    public Order create(Long productId, Long userId, Integer quantity) {
        int qty = (quantity == null) ? 1 : quantity;
        if (qty <= 0) {
            throw new BusinessException("quantity must be positive");
        }
        if (!users.existsById(userId)) {
            throw new NotFoundException("User " + userId + " not found");
        }
        Product product = products.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product " + productId + " not found"));
        if (product.getStockQuantity() < qty) {
            throw new BusinessException("Not enough stock for product " + productId
                    + " (requested " + qty + ", available " + product.getStockQuantity() + ")");
        }

        product.setStockQuantity(product.getStockQuantity() - qty);
        products.save(product);

        BigDecimal total = product.getPrice().multiply(BigDecimal.valueOf(qty));
        OrderItem item = new OrderItem(productId, qty, product.getPrice());
        Order order = new Order(userId, OrderStatus.NEW, total, Instant.now(), Set.of(item));
        return orders.save(order);
    }

    /** Отмена заказа с возвратом остатков на склад. Идемпотентна. */
    @Transactional
    public Order cancel(Long orderId) {
        Order order = getById(orderId);
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return order;
        }
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new BusinessException("Cannot cancel a delivered order " + orderId);
        }
        for (OrderItem item : order.getItems()) {
            products.findById(item.getProductId()).ifPresent(p -> {
                p.setStockQuantity(p.getStockQuantity() + item.getQuantity());
                products.save(p);
            });
        }
        order.setStatus(OrderStatus.CANCELLED);
        return orders.save(order);
    }
}
