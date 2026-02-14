package com.example.PGS.entity;


import com.example.PGS.entity.enums.PaymentGateway;
import com.example.PGS.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "callback_audit_logs",
        indexes = {
                @Index(name = "idx_callback_ref",        columnList = "reference_number"),  // ← fixed: DB column name
                @Index(name = "idx_callback_gateway",    columnList = "gateway"),
                @Index(name = "idx_callback_received_at", columnList = "received_at")       // ← fixed: DB column name
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CallbackAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference_number", nullable = false)
    private String referenceNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentGateway gateway;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(name = "raw_payload", nullable = false, length = 2000)
    private String rawPayload;

    @Column(nullable = false, length = 512)
    private String signature;

    @Column(name = "client_ip", nullable = false)
    private String clientIp;

    @CreationTimestamp
    @Column(name = "received_at")
    private LocalDateTime receivedAt;
}