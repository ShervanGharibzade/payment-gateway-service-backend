package com.example.PGS.security.callback;

import com.example.PGS.entity.CallbackNonce;
import com.example.PGS.exception.InvalidCallbackSignatureException;
import com.example.PGS.repository.CallbackNonceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CallbackNonceValidator {

    private final CallbackNonceRepository repository;

    public void validateAndStore(String nonce) {

        if (repository.existsByNonce(nonce)) {
            throw new InvalidCallbackSignatureException("Replay attack detected");
        }


        CallbackNonce callback = CallbackNonce.builder()
                        .nonce(nonce)
                        .build();

        repository.save(callback);
    }
}
