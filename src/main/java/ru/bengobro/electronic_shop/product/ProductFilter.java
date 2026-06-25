package ru.bengobro.electronic_shop.product;

import java.math.BigDecimal;

/** Критерии фильтрации товаров (любое поле может быть null — тогда не учитывается). */
public record ProductFilter(
        String category,
        String brand,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Integer minMemoryGb,
        String color
) {
}
