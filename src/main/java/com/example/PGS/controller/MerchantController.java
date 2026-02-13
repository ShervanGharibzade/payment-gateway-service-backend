package com.example.PGS.controller;

import com.example.PGS.dto.CreateMerchantRequest;
import com.example.PGS.dto.MerchantResponse;
import com.example.PGS.service.MerchantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @PostMapping
    public ResponseEntity<MerchantResponse> create(
            @Valid @RequestBody CreateMerchantRequest request
    ) {
        return ResponseEntity.ok(
                merchantService.create(request)
        );
    }
}
