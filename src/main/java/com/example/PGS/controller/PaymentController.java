package com.example.PGS.controller;

import com.example.PGS.dto.CreatePaymentRequest;
import com.example.PGS.dto.CreatePaymentResponse;
import com.example.PGS.entity.Payment;
import com.example.PGS.security.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<CreatePaymentResponse> createPayment(
            @RequestHeader("X-API-KEY") String apiKey,
            @Valid @RequestBody CreatePaymentRequest request
    ) {
        Payment payment = paymentService.createPayment(apiKey, request);

        CreatePaymentResponse response = CreatePaymentResponse.builder()
                .paymentId(payment.getId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .gateway(payment.getGateway())
                .referenceNumber(payment.getReferenceNumber())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}