package ru.bengobro.electronic_shop.eval;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Проверяет целостность золотого датасета (структура, ссылки на инструменты, категории, размер). */
class GoldenDatasetValidationTest {

    /** Имена зарегистрированных инструментов агента. */
    private static final Set<String> TOOLS = Set.of(
            "getProducts", "getProductById", "getProductCount",
            "createOrder", "getOrderStatus", "cancelOrder", "getUserOrders",
            "addToCart", "getCart", "createPayment", "getUserById");

    private static final Set<String> CATEGORIES = Set.of(
            "read", "write", "aggregation", "complex_filter", "ambiguous");

    private final List<GoldenCase> cases = GoldenDatasetLoader.load();

    @Test
    void sizeIsWithinExpectedRange() {
        assertThat(cases).hasSizeBetween(50, 100);
    }

    @Test
    void idsAreUnique() {
        assertThat(cases.stream().map(GoldenCase::id).distinct().count())
                .isEqualTo(cases.size());
    }

    @Test
    void everyCaseIsWellFormed() {
        for (GoldenCase c : cases) {
            assertThat(c.id()).as("id").isNotBlank();
            assertThat(c.query()).as("query of %s", c.id()).isNotBlank();
            assertThat(c.expectedParams()).as("expectedParams of %s", c.id()).isNotNull();
            assertThat(TOOLS).as("expectedTool of %s", c.id()).contains(c.expectedTool());
            assertThat(CATEGORIES).as("category of %s", c.id()).contains(c.category());
        }
    }

    @Test
    void allCategoriesAreCovered() {
        Set<String> present = cases.stream().map(GoldenCase::category).collect(java.util.stream.Collectors.toSet());
        assertThat(present).isEqualTo(CATEGORIES);
    }
}
