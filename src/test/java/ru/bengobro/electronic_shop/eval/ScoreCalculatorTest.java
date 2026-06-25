package ru.bengobro.electronic_shop.eval;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

import org.junit.jupiter.api.Test;

class ScoreCalculatorTest {

    private final ScoreCalculator calculator = new ScoreCalculator();

    @Test
    void weightedSumIsComputed() {
        assertThat(calculator.score(1.0, 1.0, 1.0)).isEqualTo(1.0);
        assertThat(calculator.score(0.0, 0.0, 0.0)).isEqualTo(0.0);
        // 0.40*1 + 0.35*0.5 + 0.25*0.9 = 0.8
        assertThat(calculator.score(1.0, 0.5, 0.9)).isCloseTo(0.8, offset(1e-9));
    }

    @Test
    void correctnessUsesThreshold() {
        assertThat(calculator.isCorrect(0.8)).isTrue();
        assertThat(calculator.isCorrect(0.85)).isTrue();
        assertThat(calculator.isCorrect(0.79)).isFalse();
    }
}
