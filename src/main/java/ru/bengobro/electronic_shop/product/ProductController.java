package ru.bengobro.electronic_shop.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductResponse> search(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minMemoryGb,
            @RequestParam(required = false) String color) {
        ProductFilter filter = new ProductFilter(category, brand, minPrice, maxPrice, minMemoryGb, color);
        return service.search(filter).stream().map(ProductResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable Long id) {
        return ProductResponse.from(service.getById(id));
    }

    @GetMapping("/count")
    public Map<String, Long> count(@RequestParam(required = false) String category) {
        return Map.of("count", service.countInStock(category));
    }
}
