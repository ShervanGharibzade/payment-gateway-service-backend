package com.example.PGS.security;

import com.example.PGS.dto.CallbackRequest;
import com.example.PGS.entity.CallbackAuditLog;
import com.example.PGS.repository.CallbackAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CallbackAuditLogService {

    private final CallbackAuditLogRepository repository;

    public void log(
            CallbackRequest request,
            String rawPayload,
            String signature,
            String clientIp
    ) {
        CallbackAuditLog log = CallbackAuditLog.builder()
                .referenceNumber(request.getReferenceNumber())
                .gateway(request.getGateway())
                .status(request.getStatus())
                .rawPayload(rawPayload)
                .signature(signature)
                .clientIp(clientIp)
                .build()
        ;


        repository.save(log);
    }
}
