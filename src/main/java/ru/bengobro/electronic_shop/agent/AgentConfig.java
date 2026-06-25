package ru.bengobro.electronic_shop.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация AI-агента поверх автоконфигурируемой модели GigaChat.
 *
 * <p>Регистрирует все доменные инструменты (tool-адаптеры) и системный промпт.
 */
@Configuration
public class AgentConfig {

    /**
     * Возвращает ошибку выполнения инструмента как валидный JSON.
     * Без этого GigaChat отклоняет результат функции (HTTP 422), если он не является JSON.
     */
    @Bean
    public ToolExecutionExceptionProcessor toolExecutionExceptionProcessor() {
        return exception -> {
            String message = exception.getMessage();
            return "{\"error\":\"" + (message == null ? "tool execution failed"
                    : message.replace("\\", "\\\\").replace("\"", "\\\"")) + "\"}";
        };
    }

    /**
     * {@link ChatClient} агента с подключёнными инструментами. {@link ChatModel}
     * предоставляется стартером {@code chat.giga:spring-ai-starter-model-gigachat}.
     */
    @Bean
    public ChatClient agentChatClient(ChatModel chatModel,
                                      ProductTools productTools,
                                      OrderTools orderTools,
                                      CartTools cartTools,
                                      PaymentTools paymentTools,
                                      UserTools userTools) {
        return ChatClient.builder(chatModel)
                .defaultSystem("""
                        Ты — ассистент интернет-магазина электроники.
                        Используй доступные инструменты, чтобы найти товары, оформить и оплатить заказы,
                        работать с корзиной и узнавать статусы. Вызывай инструмент, подходящий запросу,
                        и аккуратно извлекай параметры из текста пользователя.
                        Отвечай кратко и по делу на русском языке.""")
                .defaultTools(productTools, orderTools, cartTools, paymentTools, userTools)
                .build();
    }
}
