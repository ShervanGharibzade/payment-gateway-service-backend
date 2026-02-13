package com.example.PGS.dto;

import com.example.PGS.entity.enums.PaymentGateway;
import com.example.PGS.entity.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CallbackRequest {
    @NotBlank
    @Size(max = 100)
    private String referenceNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentGateway gateway;

    @NotNull
    private PaymentStatus status;

    @NotNull
    @Positive
    @Schema(description = "Payment amount", example = "150000")
    private BigDecimal amount;

    @NotNull
    @Schema(description = "Unix timestamp (seconds)")
    private Long timestamp;

    @NotBlank
    @Schema(description = "Unique nonce for replay protection")
    private String nonce;


    @Size(max = 100)
    private String trackingNumber;

    @Size(max = 100)
    private String failureReason;

    @Size(max = 255)
    private String message;



    public String toSignaturePayload() {
        return referenceNumber + "|" +
                amount.stripTrailingZeros().toPlainString() + "|" +
                status.name() + "|" +
                timestamp + "|" +
                nonce;
    }

    public boolean isSuccessful() {
        return this.status == PaymentStatus.SUCCESS;
    }
}
