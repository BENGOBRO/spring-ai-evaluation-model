package ru.bengobro.electronic_shop.agent;

import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * Точка входа в агента: выполняет запрос пользователя и возвращает {@link AgentInvocation}
 * с финальным текстом и зафиксированными вызовами инструментов.
 */
@Service
public class AgentService {

    private final ChatClient agentChatClient;
    private final AgentInvocationRecorder recorder;

    public AgentService(ChatClient agentChatClient, AgentInvocationRecorder recorder) {
        this.agentChatClient = agentChatClient;
        this.recorder = recorder;
    }

    public AgentInvocation chat(String query) {
        recorder.start();
        try {
            String response = agentChatClient.prompt()
                    .user(query)
                    .call()
                    .content();
            List<ToolCall> toolCalls = recorder.snapshot();
            return new AgentInvocation(query, response, toolCalls);
        } finally {
            recorder.clear();
        }
    }
}
