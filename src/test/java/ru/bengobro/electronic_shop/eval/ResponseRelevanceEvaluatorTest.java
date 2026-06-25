package ru.bengobro.electronic_shop.eval;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

import org.junit.jupiter.api.Test;

/** Тестирует разбор ответа судьи (без обращения к LLM). */
class ResponseRelevanceEvaluatorTest {

    @Test
    void parsesPlainNumber() {
        assertThat(ResponseRelevanceEvaluator.parseScore("0.9")).isCloseTo(0.9, offset(1e-9));
    }

    @Test
    void extractsNumberFromText() {
        assertThat(ResponseRelevanceEvaluator.parseScore("Оценка: 0.85")).isCloseTo(0.85, offset(1e-9));
    }

    @Test
    void clampsAboveOne() {
        assertThat(ResponseRelevanceEvaluator.parseScore("2")).isEqualTo(1.0);
    }

    @Test
    void fallsBackToZeroWhenNoNumber() {
        assertThat(ResponseRelevanceEvaluator.parseScore("не могу оценить")).isEqualTo(0.0);
    }

    @Test
    void fallsBackToZeroOnNull() {
        assertThat(ResponseRelevanceEvaluator.parseScore(null)).isEqualTo(0.0);
    }
}
