package ru.bengobro.electronic_shop.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация AI-агента поверх автоконфигурируемой моделью GigaChat.
 *
 * <p>На шаге 1 здесь объявлен только базовый {@link ChatClient} агента.
 * Инструменты ({@code @Tool}) и системный промпт добавляются на шаге 4,
 * Advisor с retry — на шаге 7.
 */
@Configuration
public class AgentConfig {

    /**
     * Базовый {@link ChatClient} агента. {@link ChatModel} предоставляется
     * стартером {@code chat.giga:spring-ai-starter-model-gigachat}.
     */
    @Bean
    public ChatClient agentChatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("Ты — ассистент интернет-магазина электроники. "
                        + "Отвечай кратко и по делу.")
                .build();
    }
}
