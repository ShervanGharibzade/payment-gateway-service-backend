package com.example.PGS.repository;

import com.example.PGS.entity.CallbackAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CallbackAuditLogRepository
        extends JpaRepository<CallbackAuditLog, Long> {
}
