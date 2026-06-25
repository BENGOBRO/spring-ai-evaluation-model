package ru.bengobro.electronic_shop.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * Потоково-локальный рекордер вызовов инструментов в рамках одного обращения к агенту.
 *
 * <p>{@code ChatClient.call()} исполняет tool-вызовы синхронно в том же потоке, поэтому
 * {@link ThreadLocal} корректно собирает все вызовы между {@link #start()} и {@link #clear()}.
 */
@Component
public class AgentInvocationRecorder {

    private final ThreadLocal<List<ToolCall>> calls = new ThreadLocal<>();

    /** Начать запись для текущего обращения. */
    public void start() {
        calls.set(new ArrayList<>());
    }

    /** Зафиксировать вызов инструмента (вызывается из tool-адаптеров). */
    public void record(String tool, Map<String, Object> arguments) {
        List<ToolCall> current = calls.get();
        if (current != null) {
            current.add(new ToolCall(tool, arguments));
        }
    }

    /** Текущий снимок вызовов (копия). */
    public List<ToolCall> snapshot() {
        List<ToolCall> current = calls.get();
        return (current == null) ? List.of() : List.copyOf(current);
    }

    /** Очистить запись (обязательно в finally). */
    public void clear() {
        calls.remove();
    }
}
