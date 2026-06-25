package ru.bengobro.electronic_shop.eval;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

/** Подсчёт агрегатов эксперимента и отрисовка сводки. */
final class ExperimentReport {

    private ExperimentReport() {
    }

    /** Сводка по одной конфигурации. */
    record Summary(
            String label,
            int total,
            int correct,
            double accuracyPercent,
            double avgC1,
            double avgC2,
            double avgC3,
            Map<String, Double> accuracyByCategory,
            Double avgAttempts) {
    }

    static Summary summarize(String label, Map<String, String> idToCategory,
                             List<EvaluationResult> results, Double avgAttempts) {
        int total = results.size();
        int correct = (int) results.stream().filter(EvaluationResult::correct).count();

        Map<String, List<EvaluationResult>> byCategory = results.stream()
                .collect(Collectors.groupingBy(r -> idToCategory.getOrDefault(r.queryId(), "?")));
        Map<String, Double> accuracyByCategory = new TreeMap<>();
        byCategory.forEach((category, list) -> accuracyByCategory.put(category,
                percent((int) list.stream().filter(EvaluationResult::correct).count(), list.size())));

        return new Summary(label, total, correct, percent(correct, total),
                avg(results, EvaluationResult::c1),
                avg(results, EvaluationResult::c2),
                avg(results, EvaluationResult::c3),
                accuracyByCategory, avgAttempts);
    }

    static String toMarkdown(Summary baseline, Summary advisor, String date) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Результаты эксперимента\n\n");
        sb.append("Дата прогона: ").append(date).append("  \n");
        sb.append("Размер датасета: ").append(baseline.total()).append("\n\n");

        sb.append("## Сводка\n\n");
        sb.append("| Метрика | baseline | with-advisor | Δ |\n");
        sb.append("|---|---|---|---|\n");
        row(sb, "Accuracy, %", baseline.accuracyPercent(), advisor.accuracyPercent());
        row(sb, "Корректных", baseline.correct(), advisor.correct());
        row(sb, "Средн. C1", baseline.avgC1(), advisor.avgC1());
        row(sb, "Средн. C2", baseline.avgC2(), advisor.avgC2());
        row(sb, "Средн. C3", baseline.avgC3(), advisor.avgC3());
        sb.append("| Средн. попыток | — | ")
                .append(fmt(advisor.avgAttempts() == null ? 0.0 : advisor.avgAttempts())).append(" | — |\n");

        sb.append("\n## Accuracy по категориям, %\n\n");
        sb.append("| Категория | baseline | with-advisor | Δ |\n");
        sb.append("|---|---|---|---|\n");
        TreeMap<String, Double> categories = new TreeMap<>(baseline.accuracyByCategory());
        categories.putAll(advisor.accuracyByCategory());
        for (String category : categories.keySet()) {
            double b = baseline.accuracyByCategory().getOrDefault(category, 0.0);
            double a = advisor.accuracyByCategory().getOrDefault(category, 0.0);
            row(sb, category, b, a);
        }
        return sb.toString();
    }

    private static void row(StringBuilder sb, String name, double baseline, double advisor) {
        sb.append("| ").append(name).append(" | ")
                .append(fmt(baseline)).append(" | ")
                .append(fmt(advisor)).append(" | ")
                .append(fmt(advisor - baseline)).append(" |\n");
    }

    private static double avg(List<EvaluationResult> results, ToDoubleFunction<EvaluationResult> field) {
        return results.stream().mapToDouble(field).average().orElse(0.0);
    }

    private static double percent(int part, int total) {
        return total == 0 ? 0.0 : 100.0 * part / total;
    }

    private static String fmt(double value) {
        return String.format(Locale.US, "%.2f", value);
    }
}
