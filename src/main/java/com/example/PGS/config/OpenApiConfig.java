package com.example.PGS.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Payment Gateway Service",
                version = "1.0",
                description = "PGS API Documentation"
        )
)
public class OpenApiConfig {
}
