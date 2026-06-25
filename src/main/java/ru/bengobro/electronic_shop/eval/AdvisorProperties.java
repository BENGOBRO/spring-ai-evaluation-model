package ru.bengobro.electronic_shop.eval;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки Advisor'а с retry.
 *
 * @param enabled     включён ли повтор (для конфигурации эксперимента)
 * @param maxAttempts максимальное число попыток (>= 1), защита от бесконечного цикла
 */
@ConfigurationProperties(prefix = "evaluation.advisor")
public record AdvisorProperties(boolean enabled, Integer maxAttempts) {

    public AdvisorProperties {
        if (maxAttempts == null || maxAttempts < 1) {
            maxAttempts = 1;
        }
    }
}
