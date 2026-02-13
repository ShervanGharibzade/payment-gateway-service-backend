package com.example.PGS.security.callback;

import com.example.PGS.exception.InvalidCallbackSignatureException;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class CallbackTimestampValidator {

    private static final long ALLOWED_DRIFT_SECONDS = 300; // 5 min

    public void validate(Long timestamp) {
        long now = Instant.now().getEpochSecond();

        if (Math.abs(now - timestamp) > ALLOWED_DRIFT_SECONDS) {
            throw new InvalidCallbackSignatureException("Expired callback");
        }
    }
}
