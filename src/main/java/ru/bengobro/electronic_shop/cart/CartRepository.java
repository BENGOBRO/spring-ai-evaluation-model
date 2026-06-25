package ru.bengobro.electronic_shop.cart;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface CartRepository extends CrudRepository<Cart, Long> {

    Optional<Cart> findByUserId(Long userId);
}
