package ru.bengobro.electronic_shop.web;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Метаданные OpenAPI для Swagger UI (ручное тестирование API). */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI electronicShopOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Electronic Shop API")
                .version("0.0.1")
                .description("REST API интернет-магазина электроники и AI-агента"));
    }
}
