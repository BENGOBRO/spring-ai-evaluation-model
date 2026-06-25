package ru.bengobro.electronic_shop.eval;

import org.springframework.stereotype.Component;

/**
 * Свёртка критериев в итоговый балл: {@code Score = 0.40·C1 + 0.35·C2 + 0.25·C3}.
 * Ответ считается корректным при {@code Score >= 0.8}.
 */
@Component
public class ScoreCalculator {

    public static final double WEIGHT_TOOL = 0.40;
    public static final double WEIGHT_PARAM = 0.35;
    public static final double WEIGHT_RELEVANCE = 0.25;
    public static final double CORRECT_THRESHOLD = 0.8;

    public double score(double c1, double c2, double c3) {
        return WEIGHT_TOOL * c1 + WEIGHT_PARAM * c2 + WEIGHT_RELEVANCE * c3;
    }

    public boolean isCorrect(double score) {
        return score >= CORRECT_THRESHOLD;
    }
}
