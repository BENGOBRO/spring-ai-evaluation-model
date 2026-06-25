package ru.bengobro.electronic_shop.payment;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<Payment, Long> {

    List<Payment> findByOrderId(Long orderId);
}
