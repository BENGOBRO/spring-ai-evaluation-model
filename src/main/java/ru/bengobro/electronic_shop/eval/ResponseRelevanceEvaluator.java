package ru.bengobro.electronic_shop.eval;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * C3 — Response relevance: оценка релевантности финального текста запросу пользователя
 * отдельной моделью (LLM-as-a-Judge). Возвращает значение в диапазоне [0;1].
 */
@Component
public class ResponseRelevanceEvaluator {

    private static final Logger log = LoggerFactory.getLogger(ResponseRelevanceEvaluator.class);
    private static final Pattern NUMBER = Pattern.compile("\\d*\\.?\\d+");

    private final ChatClient judge;

    public ResponseRelevanceEvaluator(@Qualifier("judgeChatClient") ChatClient judge) {
        this.judge = judge;
    }

    public double evaluate(String query, String response) {
        String prompt = """
                Запрос пользователя: %s
                Ответ ассистента: %s
                Оцени релевантность ответа числом от 0 до 1.""".formatted(query, response);
        String raw = judge.prompt().user(prompt).call().content();
        return parseScore(raw);
    }

    /** Извлекает число [0;1] из ответа судьи; на ошибку парсинга — безопасное 0.0. */
    static double parseScore(String raw) {
        if (raw == null) {
            log.warn("Judge returned null response; falling back to 0.0");
            return 0.0;
        }
        Matcher matcher = NUMBER.matcher(raw);
        if (!matcher.find()) {
            log.warn("No number found in judge response '{}'; falling back to 0.0", raw);
            return 0.0;
        }
        try {
            double value = Double.parseDouble(matcher.group());
            return Math.max(0.0, Math.min(1.0, value));
        } catch (NumberFormatException e) {
            log.warn("Failed to parse judge score from '{}'; falling back to 0.0", raw);
            return 0.0;
        }
    }
}
