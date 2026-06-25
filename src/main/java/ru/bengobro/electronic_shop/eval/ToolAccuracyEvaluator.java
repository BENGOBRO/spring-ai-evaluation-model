package ru.bengobro.electronic_shop.eval;

import org.springframework.stereotype.Component;

/** C1 — Tool accuracy: совпал ли вызванный инструмент с эталонным. Бинарно 0/1. */
@Component
public class ToolAccuracyEvaluator {

    public double evaluate(String calledTool, String expectedTool) {
        return (expectedTool != null && expectedTool.equals(calledTool)) ? 1.0 : 0.0;
    }
}
