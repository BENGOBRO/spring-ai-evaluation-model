package ru.bengobro.electronic_shop.product;

import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {

    List<Product> findByCategory(String category);

    long countByStockQuantityGreaterThan(int quantity);

    @Query("SELECT count(*) FROM product WHERE category = :category AND stock_quantity > 0")
    long countInStockByCategory(String category);
}
