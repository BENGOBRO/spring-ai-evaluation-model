package ru.bengobro.electronic_shop.eval;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Эксперимент: прогон золотого датасета в двух конфигурациях (baseline и with-advisor),
 * подсчёт Accuracy и средних C1/C2/C3, сохранение отчёта в docs/results.
 *
 * <p>Дорогой live-прогон (вызовы GigaChat) и сбрасывает БД, поэтому по умолчанию выключен.
 * Запуск вручную: {@code ./mvnw test -Dtest=ExperimentRunner -Dexperiment=true}.
 */
@SpringBootTest(properties = {
        "spring.flyway.clean-disabled=false",
        "evaluation.advisor.enabled=true",
        "evaluation.advisor.max-attempts=2"
})
@Tag("experiment")
@EnabledIfSystemProperty(named = "experiment", matches = "true")
class ExperimentRunner {

    private static final Logger log = LoggerFactory.getLogger(ExperimentRunner.class);
    private static final Path RESULTS_DIR = Path.of("docs", "results");

    @Autowired
    EvaluationRetryAdvisor advisor;

    @Autowired
    Flyway flyway;

    @Test
    void runBaselineVsAdvisor() throws IOException {
        String dataset = System.getProperty("experiment.dataset", "/golden-dataset.json");
        List<GoldenCase> cases = GoldenDatasetLoader.load(dataset);
        log.info("Loaded {} cases from {}", cases.size(), dataset);
        Map<String, String> idToCategory = cases.stream()
                .collect(Collectors.toMap(GoldenCase::id, GoldenCase::category));

        log.info("Running baseline over {} cases", cases.size());
        resetDatabase();
        List<EvaluationResult> baseline = new ArrayList<>();
        for (GoldenCase c : cases) {
            baseline.add(advisor.runOnce(c.query(), c.id(), c.expectedTool(), c.expectedParams()));
        }

        log.info("Running with-advisor over {} cases", cases.size());
        resetDatabase();
        List<EvaluationResult> withAdvisor = new ArrayList<>();
        int totalAttempts = 0;
        for (GoldenCase c : cases) {
            RetryOutcome outcome = advisor.runWithRetry(c.query(), c.id(), c.expectedTool(), c.expectedParams());
            withAdvisor.add(outcome.result());
            totalAttempts += outcome.attempts();
        }
        double avgAttempts = (double) totalAttempts / cases.size();

        ExperimentReport.Summary baselineSummary =
                ExperimentReport.summarize("baseline", idToCategory, baseline, null);
        ExperimentReport.Summary advisorSummary =
                ExperimentReport.summarize("with-advisor", idToCategory, withAdvisor, avgAttempts);

        writeReports(baseline, withAdvisor, baselineSummary, advisorSummary);
        log.info("Baseline accuracy {}%, with-advisor accuracy {}%",
                baselineSummary.accuracyPercent(), advisorSummary.accuracyPercent());
    }

    private void writeReports(List<EvaluationResult> baseline, List<EvaluationResult> withAdvisor,
                              ExperimentReport.Summary baselineSummary,
                              ExperimentReport.Summary advisorSummary) throws IOException {
        Files.createDirectories(RESULTS_DIR);
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(RESULTS_DIR.resolve("baseline.json").toFile(), baseline);
        mapper.writeValue(RESULTS_DIR.resolve("with-advisor.json").toFile(), withAdvisor);
        Files.writeString(RESULTS_DIR.resolve("summary.md"),
                ExperimentReport.toMarkdown(baselineSummary, advisorSummary, LocalDate.now().toString()));
        log.info("Reports written to {}", RESULTS_DIR.toAbsolutePath());
    }

    /** Возврат БД к seed-состоянию между конфигурациями для воспроизводимости. */
    private void resetDatabase() {
        flyway.clean();
        flyway.migrate();
    }
}
