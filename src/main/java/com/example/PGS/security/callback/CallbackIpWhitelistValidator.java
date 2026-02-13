package com.example.PGS.security.callback;

import com.example.PGS.exception.InvalidCallbackSignatureException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CallbackIpWhitelistValidator {

    public void validate(String clientIp, List<String> allowedIps) {

        if (!allowedIps.contains(clientIp)) {
            throw new InvalidCallbackSignatureException("IP not allowed: " + clientIp);
        }
    }
}
