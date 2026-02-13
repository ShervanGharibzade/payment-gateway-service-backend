package com.example.PGS.dto;

import com.example.PGS.entity.enums.PaymentGateway;
import com.example.PGS.entity.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class CreatePaymentResponse {

    private Long paymentId;

    private BigDecimal amount;

    private PaymentStatus status;

    private PaymentGateway gateway;

    private String referenceNumber;


}