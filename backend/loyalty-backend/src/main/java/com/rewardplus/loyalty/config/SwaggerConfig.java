package com.rewardplus.loyalty.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI configuration for API documentation.
 * Provides interactive API documentation accessible at /swagger-ui.html
 */
@Configuration
public class SwaggerConfig {

    @Value("${app.name:RewardPlus Loyalty Program}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    /**
     * Configures the OpenAPI specification with metadata and security schemes.
     */
    @Bean
    public OpenAPI loyaltyOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title(appName + " API")
                .description("Enterprise Customer Loyalty Program System - REST API Documentation. " +
                    "This API provides endpoints for customer management, loyalty points, " +
                    "transactions, rewards, promotions, and analytics.")
                .version(appVersion)
                .contact(new Contact()
                    .name("RewardPlus Technical Team")
                    .email("support@rewardplus.com")
                    .url("https://www.rewardplus.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("http://www.apache.org/licenses/LICENSE-2.0")))
            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
            .components(new Components()
                .addSecuritySchemes("Bearer Authentication", 
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Enter your JWT token in the format: Bearer {token}")));
    }
}

