package ru.bengobro.electronic_shop.eval;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.bengobro.electronic_shop.agent.AgentInvocation;
import ru.bengobro.electronic_shop.agent.AgentService;

/**
 * Advisor с повтором: при {@code Score < 0.8} формирует обратную связь и переспрашивает агента
 * (генерация → оценка → retry с фидбеком → повтор до порога либо лимита {@code maxAttempts}).
 * Возвращает лучший по Score результат.
 *
 * <p>Использует полную многокритериальную оценку (включая C1/C2), поэтому работает в контексте
 * эксперимента, где доступен эталон. Обратная связь — обобщённая (не раскрывает эталонные значения).
 */
@Component
public class EvaluationRetryAdvisor {

    private static final Logger log = LoggerFactory.getLogger(EvaluationRetryAdvisor.class);

    private final AgentService agent;
    private final Evaluator evaluator;
    private final AdvisorProperties properties;

    public EvaluationRetryAdvisor(AgentService agent, Evaluator evaluator, AdvisorProperties properties) {
        this.agent = agent;
        this.evaluator = evaluator;
        this.properties = properties;
    }

    /** Один проход без повтора (baseline). */
    public EvaluationResult runOnce(String query, String queryId,
                                    String expectedTool, Map<String, Object> expectedParams) {
        AgentInvocation invocation = agent.chat(query);
        return evaluator.evaluate(invocation, queryId, expectedTool, expectedParams);
    }

    /** Прогон с повтором при Score < порога, не более {@code maxAttempts} попыток. */
    public RetryOutcome runWithRetry(String query, String queryId,
                                     String expectedTool, Map<String, Object> expectedParams) {
        int maxAttempts = Math.max(1, properties.maxAttempts());
        EvaluationResult best = null;
        int attempts = 0;
        String feedback = null;

        while (attempts < maxAttempts) {
            attempts++;
            String input = (feedback == null) ? query
                    : query + System.lineSeparator() + System.lineSeparator() + feedback;

            AgentInvocation invocation = agent.chat(input);
            EvaluationResult result = evaluator.evaluate(invocation, queryId, expectedTool, expectedParams);

            if (best == null || result.score() > best.score()) {
                best = result;
            }
            if (result.correct()) {
                break;
            }
            log.debug("Attempt {} for {} scored {} (< threshold); retrying", attempts, queryId, result.score());
            feedback = buildFeedback(result);
        }
        return new RetryOutcome(best, attempts);
    }

    /** Обобщённая подсказка по проблемному критерию, без раскрытия эталона. */
    private String buildFeedback(EvaluationResult result) {
        StringBuilder sb = new StringBuilder("Твой предыдущий ответ признан недостаточным. ");
        if (result.c1() < 1.0) {
            sb.append("Перепроверь выбор инструмента — он должен точно соответствовать запросу. ");
        }
        if (result.c2() < 1.0) {
            sb.append("Внимательно извлеки все необходимые параметры из текста запроса. ");
        }
        if (result.c3() < 1.0) {
            sb.append("Сформулируй ответ строго по существу запроса. ");
        }
        return sb.append("Ответь корректно.").toString();
    }
}
