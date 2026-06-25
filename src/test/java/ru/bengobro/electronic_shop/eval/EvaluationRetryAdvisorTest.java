package ru.bengobro.electronic_shop.eval;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import ru.bengobro.electronic_shop.agent.AgentInvocation;
import ru.bengobro.electronic_shop.agent.AgentService;

class EvaluationRetryAdvisorTest {

    private final AgentService agent = mock(AgentService.class);
    private final Evaluator evaluator = mock(Evaluator.class);
    private final AgentInvocation invocation = new AgentInvocation("q", "r", List.of());

    private EvaluationRetryAdvisor advisor(int maxAttempts) {
        return new EvaluationRetryAdvisor(agent, evaluator, new AdvisorProperties(true, maxAttempts));
    }

    private EvaluationResult result(double score, boolean correct) {
        return new EvaluationResult("q-01", "getProducts", Map.of(),
                correct ? 1.0 : 0.0, 1.0, 1.0, score, correct);
    }

    @Test
    void doesNotRetryWhenFirstAttemptIsCorrect() {
        when(agent.chat(any())).thenReturn(invocation);
        when(evaluator.evaluate(any(), any(), any(), any())).thenReturn(result(0.9, true));

        RetryOutcome outcome = advisor(3).runWithRetry("q", "q-01", "getProducts", Map.of());

        assertThat(outcome.attempts()).isEqualTo(1);
        assertThat(outcome.result().correct()).isTrue();
        verify(agent, times(1)).chat(any());
    }

    @Test
    void retriesUntilCorrectAndReturnsThatResult() {
        when(agent.chat(any())).thenReturn(invocation);
        when(evaluator.evaluate(any(), any(), any(), any()))
                .thenReturn(result(0.5, false), result(0.85, true));

        RetryOutcome outcome = advisor(3).runWithRetry("q", "q-01", "getProducts", Map.of());

        assertThat(outcome.attempts()).isEqualTo(2);
        assertThat(outcome.result().score()).isEqualTo(0.85);
        verify(agent, times(2)).chat(any());
    }

    @Test
    void stopsAtMaxAttemptsAndReturnsBestByScore() {
        when(agent.chat(any())).thenReturn(invocation);
        when(evaluator.evaluate(any(), any(), any(), any()))
                .thenReturn(result(0.4, false), result(0.7, false), result(0.6, false));

        RetryOutcome outcome = advisor(3).runWithRetry("q", "q-01", "getProducts", Map.of());

        assertThat(outcome.attempts()).isEqualTo(3);
        assertThat(outcome.result().score()).isEqualTo(0.7);
        verify(agent, times(3)).chat(any());
    }

    @Test
    void runOnceDoesNotRetry() {
        when(agent.chat(any())).thenReturn(invocation);
        when(evaluator.evaluate(any(), any(), any(), any())).thenReturn(result(0.3, false));

        EvaluationResult result = advisor(3).runOnce("q", "q-01", "getProducts", Map.of());

        assertThat(result.score()).isEqualTo(0.3);
        verify(agent, times(1)).chat(any());
    }
}
