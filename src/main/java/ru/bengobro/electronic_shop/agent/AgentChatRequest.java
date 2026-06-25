package ru.bengobro.electronic_shop.agent;

import jakarta.validation.constraints.NotBlank;

/** Запрос к агенту. */
public record AgentChatRequest(@NotBlank String query) {
}
