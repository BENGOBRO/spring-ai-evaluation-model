package ru.bengobro.electronic_shop.eval;

import java.util.Map;

/** Результат оценки одного ответа агента. */
public record EvaluationResult(
        String queryId,
        String calledTool,
        Map<String, Object> calledParams,
        double c1,
        double c2,
        double c3,
        double score,
        boolean correct
) {
}
