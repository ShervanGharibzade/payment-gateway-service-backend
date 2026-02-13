package com.example.PGS.controller;

import com.example.PGS.dto.CallbackRequest;
import com.example.PGS.security.CallbackAuditLogService;
import com.example.PGS.security.PaymentService;
import com.example.PGS.security.callback.CallbackSecurityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments/callback")
@RequiredArgsConstructor
public class PaymentCallbackController {

    private final CallbackSecurityService callbackSecurityService;
    private final PaymentService paymentService;
    private final CallbackAuditLogService  callbackAuditLogService;

    @PostMapping
    public ResponseEntity<String> callback(
            @RequestBody @Valid CallbackRequest request,
            @RequestHeader("X-SIGNATURE") String signature,
            HttpServletRequest httpRequest
    ) {
        callbackSecurityService.validate(httpRequest, request, signature);

        callbackAuditLogService.log(
                request,
                request.toSignaturePayload(),
                signature,
                httpRequest.getRemoteAddr()
        );

        paymentService.handleCallback(request);
        return ResponseEntity.ok("OK");
    }
}
