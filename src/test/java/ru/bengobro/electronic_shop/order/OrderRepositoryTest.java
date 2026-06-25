package ru.bengobro.electronic_shop.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ImportAutoConfiguration(FlywayAutoConfiguration.class)
class OrderRepositoryTest {

    @Autowired
    OrderRepository orders;

    @Test
    void readsSeededOrderWithItems() {
        Order order = orders.findById(1L).orElseThrow();
        assertThat(order.getUserId()).isEqualTo(1L);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(order.getItems()).hasSize(1);
    }

    @Test
    void persistsOrderAggregateWithItems() {
        Order toSave = new Order(
                1L, OrderStatus.NEW, new BigDecimal("35980.00"), Instant.now(),
                Set.of(
                        new OrderItem(1L, 1, new BigDecimal("34990.00")),
                        new OrderItem(16L, 1, new BigDecimal("8990.00"))
                ));

        Order saved = orders.save(toSave);
        assertThat(saved.getId()).isNotNull();

        Order reloaded = orders.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getItems()).hasSize(2)
                .extracting(OrderItem::getProductId)
                .containsExactlyInAnyOrder(1L, 16L);
    }

    @Test
    void findsByUserId() {
        assertThat(orders.findByUserId(12L)).hasSize(1);
    }
}
