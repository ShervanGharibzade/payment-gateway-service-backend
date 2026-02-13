package com.example.PGS.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMerchantRequest {

    @NotBlank(message = "Merchant name is required")
    @Size(min = 3, max = 100, message = "Merchant name must be between 3 and 100 characters")
    private String name;

    @NotBlank(message = "Callback URL is required")
    @Pattern(
            regexp = "^(https?)://.*$",
            message = "Callback URL must be a valid http or https URL"
    )
    private String callbackUrl;
}