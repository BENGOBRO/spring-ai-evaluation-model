package ru.bengobro.electronic_shop.eval;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ParameterAccuracyEvaluatorTest {

    private final ParameterAccuracyEvaluator evaluator = new ParameterAccuracyEvaluator();

    private static Map<String, Object> map(Object... kv) {
        Map<String, Object> m = new HashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) {
            m.put((String) kv[i], kv[i + 1]);
        }
        return m;
    }

    @Test
    void halfWhenOneOfTwoFieldsMissing() {
        Map<String, Object> expected = map("maxPrice", 2000, "category", "electronics");
        Map<String, Object> called = map("maxPrice", 2000, "category", null);
        assertThat(evaluator.evaluate(called, expected)).isEqualTo(0.5);
    }

    @Test
    void fullWhenAllFieldsMatch() {
        Map<String, Object> expected = map("maxPrice", 2000, "category", "electronics");
        Map<String, Object> called = map("maxPrice", 2000, "category", "electronics");
        assertThat(evaluator.evaluate(called, expected)).isEqualTo(1.0);
    }

    @Test
    void numericValuesMatchAcrossTypes() {
        Map<String, Object> expected = map("maxPrice", 2000);
        assertThat(evaluator.evaluate(map("maxPrice", new BigDecimal("2000")), expected)).isEqualTo(1.0);
        assertThat(evaluator.evaluate(map("maxPrice", 2000L), expected)).isEqualTo(1.0);
    }

    @Test
    void stringValuesMatchIgnoringCaseAndSpaces() {
        Map<String, Object> expected = map("brand", "apple");
        assertThat(evaluator.evaluate(map("brand", " Apple "), expected)).isEqualTo(1.0);
    }

    @Test
    void emptyExpectedIsFullWhenNoExtraValues() {
        assertThat(evaluator.evaluate(map("category", null), Map.of())).isEqualTo(1.0);
        assertThat(evaluator.evaluate(Map.of(), Map.of())).isEqualTo(1.0);
    }

    @Test
    void emptyExpectedIsZeroWhenExtraValueProvided() {
        assertThat(evaluator.evaluate(map("category", "electronics"), Map.of())).isEqualTo(0.0);
    }

    @Test
    void zeroWhenCalledParamsNull() {
        assertThat(evaluator.evaluate(null, map("orderId", 1))).isEqualTo(0.0);
    }
}
