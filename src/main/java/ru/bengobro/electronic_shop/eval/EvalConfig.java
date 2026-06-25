package ru.bengobro.electronic_shop.eval;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Конфигурация LLM-судьи (отдельный ChatClient без инструментов) для критерия C3. */
@Configuration
@EnableConfigurationProperties(AdvisorProperties.class)
public class EvalConfig {

    @Bean
    public ChatClient judgeChatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("""
                        Ты — строгий и беспристрастный оценщик качества ответов ассистента.
                        Тебе дают запрос пользователя и ответ ассистента.
                        Верни ТОЛЬКО одно число от 0 до 1 — насколько ответ релевантен запросу
                        (1 — полностью отвечает по существу, 0 — не отвечает). Без слов и пояснений.""")
                .build();
    }
}
