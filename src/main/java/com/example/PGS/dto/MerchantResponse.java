package com.example.PGS.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MerchantResponse {

    private Long id;
    private String name;
    private String apiKey;
}