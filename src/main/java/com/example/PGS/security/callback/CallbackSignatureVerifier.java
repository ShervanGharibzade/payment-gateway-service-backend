package com.example.PGS.security.callback;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CallbackSignatureVerifier {

    private static final String HMAC_SHA256 = "HmacSHA256";

    public static boolean verify(
            String payload,
            String receivedSignature,
            String secretKey
    ) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec keySpec =
                    new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            mac.init(keySpec);

            byte[] rawHmac = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String calculatedSignature =
                    Base64.getEncoder().encodeToString(rawHmac);

            return calculatedSignature.equals(receivedSignature);

        } catch (Exception e) {
            return false;
        }
    }
}
