package ru.bengobro.electronic_shop.eval;

import java.math.BigDecimal;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * C2 — Parameter accuracy: доля корректно переданных полей относительно эталона.
 *
 * <p>Для пустого эталона {@code {}} результат 1.0, если агент не передал лишних (ненулевых) полей.
 * Для непустого эталона: доля совпавших полей = верно / |expectedParams| (лишние ненулевые поля
 * сверх эталона не учитываются — следуем формуле спецификации). Сравнение значений нормализуется:
 * числа сравниваются численно, строки — без учёта регистра и пробелов по краям.
 */
@Component
public class ParameterAccuracyEvaluator {

    public double evaluate(Map<String, Object> calledParams, Map<String, Object> expectedParams) {
        if (expectedParams == null || expectedParams.isEmpty()) {
            return hasNonNullValue(calledParams) ? 0.0 : 1.0;
        }
        int matched = 0;
        for (Map.Entry<String, Object> expected : expectedParams.entrySet()) {
            Object actual = (calledParams == null) ? null : calledParams.get(expected.getKey());
            if (valuesMatch(expected.getValue(), actual)) {
                matched++;
            }
        }
        return (double) matched / expectedParams.size();
    }

    private boolean hasNonNullValue(Map<String, Object> params) {
        return params != null && params.values().stream().anyMatch(v -> v != null);
    }

    private boolean valuesMatch(Object expected, Object actual) {
        if (actual == null) {
            return expected == null;
        }
        if (expected == null) {
            return false;
        }
        BigDecimal expectedNumber = toBigDecimal(expected);
        BigDecimal actualNumber = toBigDecimal(actual);
        if (expectedNumber != null && actualNumber != null) {
            return expectedNumber.compareTo(actualNumber) == 0;
        }
        return expected.toString().trim().equalsIgnoreCase(actual.toString().trim());
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value instanceof Number number) {
            return new BigDecimal(number.toString());
        }
        try {
            return new BigDecimal(value.toString().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
