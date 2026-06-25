package ru.bengobro.electronic_shop.agent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import ru.bengobro.electronic_shop.product.ProductService;

/** Проверяет, что tool-адаптер фиксирует имя инструмента и все аргументы (включая null) — вход для C1/C2. */
class ProductToolsTest {

    @Test
    void recordsToolNameAndArgumentsIncludingNulls() {
        ProductService service = mock(ProductService.class);
        when(service.search(any())).thenReturn(List.of());
        AgentInvocationRecorder recorder = new AgentInvocationRecorder();
        ProductTools tools = new ProductTools(service, recorder);

        recorder.start();
        try {
            tools.getProducts("electronics", null, null, new BigDecimal("2000"), null, null);

            List<ToolCall> calls = recorder.snapshot();
            assertThat(calls).hasSize(1);
            ToolCall call = calls.getFirst();
            assertThat(call.tool()).isEqualTo("getProducts");
            assertThat(call.arguments())
                    .containsEntry("category", "electronics")
                    .containsEntry("maxPrice", new BigDecimal("2000"))
                    .containsEntry("brand", null)
                    .containsEntry("minPrice", null)
                    .containsEntry("minMemoryGb", null)
                    .containsEntry("color", null);
        } finally {
            recorder.clear();
        }
    }
}
