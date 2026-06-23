package ru.bengobro.electronic_shop.agent;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import ru.bengobro.electronic_shop.product.ProductFilter;
import ru.bengobro.electronic_shop.product.ProductResponse;
import ru.bengobro.electronic_shop.product.ProductService;

/** Инструменты агента над каталогом товаров. */
@Component
public class ProductTools {

    private final ProductService products;
    private final AgentInvocationRecorder recorder;

    public ProductTools(ProductService products, AgentInvocationRecorder recorder) {
        this.products = products;
        this.recorder = recorder;
    }

    @Tool(description = "Найти товары по фильтрам: категория, бренд, диапазон цены, минимальный объём памяти, цвет")
    public List<ProductResponse> getProducts(
            @ToolParam(required = false, description = "категория, например electronics, laptops, audio") String category,
            @ToolParam(required = false, description = "бренд, например Apple, Samsung") String brand,
            @ToolParam(required = false, description = "минимальная цена в рублях") BigDecimal minPrice,
            @ToolParam(required = false, description = "максимальная цена в рублях") BigDecimal maxPrice,
            @ToolParam(required = false, description = "минимальный объём памяти в ГБ") Integer minMemoryGb,
            @ToolParam(required = false, description = "цвет") String color) {
        recorder.record("getProducts", ToolArgs.of(
                "category", category, "brand", brand, "minPrice", minPrice,
                "maxPrice", maxPrice, "minMemoryGb", minMemoryGb, "color", color));
        ProductFilter filter = new ProductFilter(category, brand, minPrice, maxPrice, minMemoryGb, color);
        return products.search(filter).stream().map(ProductResponse::from).toList();
    }

    @Tool(description = "Получить карточку товара по его идентификатору")
    public ProductResponse getProductById(
            @ToolParam(description = "идентификатор товара") Long productId) {
        recorder.record("getProductById", ToolArgs.of("productId", productId));
        return ProductResponse.from(products.getById(productId));
    }

    @Tool(description = "Сколько товаров в наличии; можно ограничить категорией")
    public long getProductCount(
            @ToolParam(required = false, description = "категория для подсчёта") String category) {
        recorder.record("getProductCount", ToolArgs.of("category", category));
        return products.countInStock(category);
    }
}
