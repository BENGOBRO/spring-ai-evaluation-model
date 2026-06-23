package ru.bengobro.electronic_shop.product;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.bengobro.electronic_shop.web.NotFoundException;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ProductService service;

    private Product sampleProduct() {
        Product p = new Product("Смартфон iPhone 15", "electronics", "Apple",
                new BigDecimal("79990.00"), 128, new BigDecimal("6.1"), "blue", 25);
        p.setId(2L);
        return p;
    }

    @Test
    void searchReturnsMappedProducts() throws Exception {
        when(service.search(any())).thenReturn(List.of(sampleProduct()));

        mvc.perform(get("/api/products").param("maxPrice", "80000").param("category", "electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].brand").value("Apple"))
                .andExpect(jsonPath("$[0].price").value(79990.00));
    }

    @Test
    void getByIdReturnsProduct() throws Exception {
        when(service.getById(2L)).thenReturn(sampleProduct());

        mvc.perform(get("/api/products/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Смартфон iPhone 15"));
    }

    @Test
    void getByIdReturns404WhenMissing() throws Exception {
        when(service.getById(99L)).thenThrow(new NotFoundException("Product 99 not found"));

        mvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Product 99 not found"));
    }

    @Test
    void countReturnsValue() throws Exception {
        when(service.countInStock(null)).thenReturn(16L);

        mvc.perform(get("/api/products/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(16));
    }
}
