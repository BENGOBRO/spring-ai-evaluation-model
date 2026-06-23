package ru.bengobro.electronic_shop.product;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String category,
        String brand,
        BigDecimal price,
        Integer memoryGb,
        BigDecimal screenDiagonal,
        String color,
        Integer stockQuantity
) {
    public static ProductResponse from(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getCategory(), p.getBrand(),
                p.getPrice(), p.getMemoryGb(), p.getScreenDiagonal(), p.getColor(), p.getStockQuantity());
    }
}
