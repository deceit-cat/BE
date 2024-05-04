package com.capstone.backend.domain.swagger.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    private static final String SECURITY_SCHME_NAME="authorization";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                .addSecuritySchemes(SECURITY_SCHME_NAME, new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")))
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Capstone API Docs")
                .description("기만고양이🐱 Contac.T")
                .version("1.0.0");
    }
}