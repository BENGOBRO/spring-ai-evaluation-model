package ru.bengobro.electronic_shop.agent;

import java.util.List;
import java.util.Optional;

/**
 * Результат одного обращения к агенту: исходный запрос, финальный текст пользователю
 * и список вызванных инструментов с параметрами.
 */
public record AgentInvocation(String query, String response, List<ToolCall> toolCalls) {

    /** Первый вызванный инструмент (для оценки обычно ожидается ровно один). */
    public Optional<ToolCall> firstToolCall() {
        return toolCalls.isEmpty() ? Optional.empty() : Optional.of(toolCalls.getFirst());
    }
}
