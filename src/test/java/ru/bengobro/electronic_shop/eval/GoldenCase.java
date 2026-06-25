package ru.bengobro.electronic_shop.eval;

import java.util.Map;

/**
 * Запись золотого датасета: эталон для одного запроса.
 *
 * @param id             уникальный идентификатор записи
 * @param category       категория запроса (read, write, aggregation, complex_filter, ambiguous)
 * @param query          текст запроса пользователя
 * @param expectedTool   ожидаемый инструмент (вход для C1)
 * @param expectedParams ожидаемые параметры инструмента (вход для C2)
 */
public record GoldenCase(
        String id,
        String category,
        String query,
        String expectedTool,
        Map<String, Object> expectedParams
) {
}
