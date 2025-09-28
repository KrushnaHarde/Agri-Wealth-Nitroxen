package com.nitroxen.demo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "AgriWealth - Polyhouse Farm Management System API",
                version = "1.0",
                description = "A comprehensive farm management system that enables efficient management of farms, polyhouses, zones, and human resources through role-based access control.",
                contact = @Contact(name = "Nitroxen", email = "support@nitroxen.com")
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("AgriWealth - Polyhouse Farm Management System API")
                        .version("1.0")
                        .description("A comprehensive farm management system API with JWT authentication and role-based access control")
                        .license(new License().name("MIT License").url("https://opensource.org/licenses/MIT"))
                );
    }
}
