package com.example.PGS.security.callback;

import com.example.PGS.config.CallbackSecurityConfig;
import com.example.PGS.dto.CallbackRequest;
import com.example.PGS.exception.InvalidCallbackSignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CallbackSecurityService {

    private final CallbackSecurityConfig config;
    private final CallbackIpWhitelistValidator ipValidator;
    private final CallbackTimestampValidator timestampValidator;
    private final CallbackNonceValidator nonceValidator;

    public void validate(
            HttpServletRequest request,
            CallbackRequest callback,
            String signature
    ) {
        ipValidator.validate(
                request.getRemoteAddr(),
                config.getAllowedIps()
        );

        timestampValidator.validate(callback.getTimestamp());

        nonceValidator.validateAndStore(callback.getNonce());

        String secret = config.getSecrets().get(callback.getGateway());
        if (secret == null) {
            throw new InvalidCallbackSignatureException(
                    "No secret configured for gateway: " + callback.getGateway()
            );
        }

        boolean valid = CallbackSignatureVerifier.verify(
                callback.toSignaturePayload(),
                signature,
                secret
        );

        if (!valid) {
            throw new InvalidCallbackSignatureException("Invalid callback signature");
        }
    }
}


