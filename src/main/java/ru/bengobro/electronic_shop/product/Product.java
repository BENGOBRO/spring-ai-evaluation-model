package ru.bengobro.electronic_shop.product;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Товар. Богатый набор параметров (цена, бренд, память, диагональ, цвет)
 * целенаправленно нагружает критерий C2 (parameter accuracy).
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED, onConstructor_ = @PersistenceCreator)
@Table("product")
public class Product {

    @Id
    private Long id;
    private String name;
    private String category;
    private String brand;
    private BigDecimal price;
    private Integer memoryGb;
    private BigDecimal screenDiagonal;
    private String color;
    private Integer stockQuantity;

    /** Бизнес-конструктор для создания нового товара (id назначает БД). */
    public Product(String name, String category, String brand, BigDecimal price,
                   Integer memoryGb, BigDecimal screenDiagonal, String color, Integer stockQuantity) {
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.memoryGb = memoryGb;
        this.screenDiagonal = screenDiagonal;
        this.color = color;
        this.stockQuantity = stockQuantity;
    }
}
