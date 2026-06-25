package ru.bengobro.electronic_shop.order;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {

    List<Order> findByUserId(Long userId);
}
