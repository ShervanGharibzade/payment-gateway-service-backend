package com.example.PGS.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    /**
     * FIXED: Use Jackson2ObjectMapperBuilderCustomizer instead of a raw @Bean ObjectMapper.
     *
     * WHY THE OLD CODE BROKE:
     *   The old version created a standalone @Bean ObjectMapper with JavaTimeModule registered.
     *   However, Spring Boot's MVC HttpMessageConverters use the AUTO-CONFIGURED ObjectMapper,
     *   not the custom bean. So the custom bean was ignored by MVC, and the auto-configured
     *   ObjectMapper had no JavaTimeModule. When GlobalExceptionHandler returned ApiErrorResponse
     *   containing a LocalDateTime field, MVC could not serialize it → HttpMessageNotWritableException
     *   → caught by the generic Exception handler → which also failed to serialize → 500.
     *
     * HOW THIS FIX WORKS:
     *   Jackson2ObjectMapperBuilderCustomizer hooks into Spring Boot's ObjectMapper builder,
     *   so ALL ObjectMapper instances (including the one used by MVC) get JavaTimeModule
     *   and WRITE_DATES_AS_TIMESTAMPS disabled. This is the correct Spring Boot way.
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder
                .modules(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}