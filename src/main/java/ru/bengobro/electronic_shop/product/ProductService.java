package ru.bengobro.electronic_shop.product;

import java.util.List;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Service;
import ru.bengobro.electronic_shop.web.NotFoundException;

@Service
public class ProductService {

    private final ProductRepository products;

    public ProductService(ProductRepository products) {
        this.products = products;
    }

    public Product getById(Long id) {
        return products.findById(id)
                .orElseThrow(() -> new NotFoundException("Product " + id + " not found"));
    }

    /** Фильтрация по набору опциональных критериев. Набор данных небольшой — фильтруем в памяти. */
    public List<Product> search(ProductFilter f) {
        return StreamSupport.stream(products.findAll().spliterator(), false)
                .filter(p -> f.category() == null || f.category().equalsIgnoreCase(p.getCategory()))
                .filter(p -> f.brand() == null || f.brand().equalsIgnoreCase(p.getBrand()))
                .filter(p -> f.minPrice() == null || p.getPrice().compareTo(f.minPrice()) >= 0)
                .filter(p -> f.maxPrice() == null || p.getPrice().compareTo(f.maxPrice()) <= 0)
                .filter(p -> f.minMemoryGb() == null
                        || (p.getMemoryGb() != null && p.getMemoryGb() >= f.minMemoryGb()))
                .filter(p -> f.color() == null || f.color().equalsIgnoreCase(p.getColor()))
                .toList();
    }

    /** Количество товаров в наличии; при указании категории — в рамках категории. */
    public long countInStock(String category) {
        return (category == null || category.isBlank())
                ? products.countByStockQuantityGreaterThan(0)
                : products.countInStockByCategory(category);
    }
}
