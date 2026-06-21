package ru.bengobro.electronic_shop.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ImportAutoConfiguration(FlywayAutoConfiguration.class)
class ProductRepositoryTest {

    @Autowired
    ProductRepository products;

    @Test
    void findsSeededProductById() {
        Product p = products.findById(2L).orElseThrow();
        assertThat(p.getName()).isEqualTo("Смартфон iPhone 15");
        assertThat(p.getCategory()).isEqualTo("electronics");
        assertThat(p.getBrand()).isEqualTo("Apple");
        assertThat(p.getPrice()).isEqualByComparingTo(new BigDecimal("79990.00"));
        assertThat(p.getMemoryGb()).isEqualTo(128);
    }

    @Test
    void findsByCategory() {
        List<Product> laptops = products.findByCategory("laptops");
        assertThat(laptops).hasSize(3)
                .extracting(Product::getBrand)
                .containsExactlyInAnyOrder("Apple", "Lenovo", "Asus");
    }

    @Test
    void countsProductsInStock() {
        assertThat(products.countByStockQuantityGreaterThan(0)).isEqualTo(16);
        assertThat(products.countInStockByCategory("electronics")).isEqualTo(5);
    }
}
