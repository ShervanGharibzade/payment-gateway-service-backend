package com.example.PGS.service;

import com.example.PGS.dto.CreateMerchantRequest;
import com.example.PGS.dto.MerchantResponse;
import com.example.PGS.entity.Merchant;
import com.example.PGS.repository.MerchantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MerchantService {

    private final MerchantRepository merchantRepository;

    public MerchantResponse create(CreateMerchantRequest request) {
        Merchant merchant = new Merchant();
        merchant.setName(request.getName());
        merchant.setCallbackUrl(request.getCallbackUrl());
        merchant.setApiKey(generateApiKey());

        Merchant saved = merchantRepository.save(merchant);

        return new MerchantResponse(
                saved.getId(),
                saved.getName(),
                saved.getApiKey()
        );
    }

    private String generateApiKey() {
        return "sk_" + UUID.randomUUID().toString().replace("-", "");
    }
}
