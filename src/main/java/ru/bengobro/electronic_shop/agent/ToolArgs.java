package ru.bengobro.electronic_shop.agent;

import java.util.LinkedHashMap;
import java.util.Map;

/** Утилита для сборки карты аргументов инструмента (допускает null-значения — это важно для C2). */
final class ToolArgs {

    private ToolArgs() {
    }

    /** Собирает карту из пар ключ/значение, сохраняя порядок и null-значения. */
    static Map<String, Object> of(Object... keyValues) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i + 1 < keyValues.length; i += 2) {
            map.put((String) keyValues[i], keyValues[i + 1]);
        }
        return map;
    }
}
