package ru.bengobro.electronic_shop.eval;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ToolAccuracyEvaluatorTest {

    private final ToolAccuracyEvaluator evaluator = new ToolAccuracyEvaluator();

    @Test
    void returnsOneWhenToolMatches() {
        assertThat(evaluator.evaluate("getProducts", "getProducts")).isEqualTo(1.0);
    }

    @Test
    void returnsZeroWhenToolDiffers() {
        assertThat(evaluator.evaluate("getProducts", "createOrder")).isEqualTo(0.0);
    }

    @Test
    void returnsZeroWhenNoToolCalled() {
        assertThat(evaluator.evaluate(null, "getProducts")).isEqualTo(0.0);
    }
}
