package ru.bengobro.electronic_shop.eval;

import java.util.Map;
import org.springframework.stereotype.Component;
import ru.bengobro.electronic_shop.agent.AgentInvocation;
import ru.bengobro.electronic_shop.agent.ToolCall;

/**
 * Оркестратор оценки: по результату работы агента и эталону считает C1, C2, C3,
 * свёртку Score и признак корректности.
 */
@Component
public class Evaluator {

    private final ToolAccuracyEvaluator toolAccuracy;
    private final ParameterAccuracyEvaluator parameterAccuracy;
    private final ResponseRelevanceEvaluator responseRelevance;
    private final ScoreCalculator scoreCalculator;

    public Evaluator(ToolAccuracyEvaluator toolAccuracy,
                     ParameterAccuracyEvaluator parameterAccuracy,
                     ResponseRelevanceEvaluator responseRelevance,
                     ScoreCalculator scoreCalculator) {
        this.toolAccuracy = toolAccuracy;
        this.parameterAccuracy = parameterAccuracy;
        this.responseRelevance = responseRelevance;
        this.scoreCalculator = scoreCalculator;
    }

    public EvaluationResult evaluate(AgentInvocation invocation, String queryId,
                                     String expectedTool, Map<String, Object> expectedParams) {
        String calledTool = invocation.firstToolCall().map(ToolCall::tool).orElse(null);
        Map<String, Object> calledParams = invocation.firstToolCall()
                .map(ToolCall::arguments).orElse(Map.of());

        double c1 = toolAccuracy.evaluate(calledTool, expectedTool);
        double c2 = parameterAccuracy.evaluate(calledParams, expectedParams);
        double c3 = responseRelevance.evaluate(invocation.query(), invocation.response());
        double score = scoreCalculator.score(c1, c2, c3);

        return new EvaluationResult(queryId, calledTool, calledParams,
                c1, c2, c3, score, scoreCalculator.isCorrect(score));
    }
}
