package ru.bengobro.electronic_shop.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.bengobro.electronic_shop.web.NotFoundException;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    OrderService service;

    private Order sampleOrder() {
        Order o = new Order(12L, OrderStatus.NEW, new BigDecimal("17990.00"), Instant.now(),
                Set.of(new OrderItem(3L, 1, new BigDecimal("17990.00"))));
        o.setId(1L);
        return o;
    }

    @Test
    void createReturns201() throws Exception {
        when(service.create(any(), any(), any())).thenReturn(sampleOrder());

        mvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":3,\"userId\":12}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.items[0].productId").value(3));
    }

    @Test
    void createReturns400WhenUserIdMissing() throws Exception {
        mvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":3}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void statusReturns404WhenOrderMissing() throws Exception {
        when(service.getById(77L)).thenThrow(new NotFoundException("Order 77 not found"));

        mvc.perform(get("/api/orders/77/status"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order 77 not found"));
    }
}
