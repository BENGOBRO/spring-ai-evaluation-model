package ru.bengobro.electronic_shop.agent;

import java.util.Map;

/**
 * Факт вызова инструмента агентом: имя инструмента и переданные (извлечённые из запроса) параметры.
 * Это вход для критериев C1 (tool accuracy) и C2 (parameter accuracy) на шаге оценки.
 */
public record ToolCall(String tool, Map<String, Object> arguments) {
}
