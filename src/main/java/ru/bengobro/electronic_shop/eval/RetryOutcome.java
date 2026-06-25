package ru.bengobro.electronic_shop.eval;

/**
 * Итог работы Advisor'а: лучший по Score результат и число фактически выполненных попыток.
 */
public record RetryOutcome(EvaluationResult result, int attempts) {
}
