package com.example.PGS.dto;

import com.example.PGS.entity.enums.PaymentGateway;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreatePaymentRequest {
    @NotNull
    private Long merchantId;

    @NotNull
    @Digits(integer = 16, fraction = 2)
    @DecimalMin(value = "0.01", inclusive = true)
    private BigDecimal amount;

    @NotBlank
    @Size(max = 100)
    private String referenceNumber;

    @Size(max = 255)
    private String description;

    @NotNull
    private PaymentGateway gateway;
}
