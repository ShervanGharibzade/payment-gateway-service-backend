package com.example.PGS.config;

import com.example.PGS.entity.enums.PaymentGateway;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "callback.security")
@Getter
@Setter
@Validated
public class CallbackSecurityConfig {

    @NotEmpty(message = "callback.security.secrets must not be empty")
    private Map<@NotNull PaymentGateway, @NotBlank String> secrets =
            new EnumMap<>(PaymentGateway.class);

    @NotEmpty(message = "callback.security.allowed-ips must not be empty")
    private List<@NotBlank String> allowedIps;

}
