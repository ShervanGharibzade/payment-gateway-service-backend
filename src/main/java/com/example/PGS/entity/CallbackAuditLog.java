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
                @Index(name = "idx_callback_ref", columnList = "referenceNumber"),
                @Index(name = "idx_callback_gateway", columnList = "gateway"),
                @Index(name = "idx_callback_received_at", columnList = "receivedAt")
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CallbackAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String referenceNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentGateway gateway;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false, length = 2000)
    private String rawPayload;

    @Column(nullable = false, length = 512)
    private String signature;

    @Column(nullable = false)
    private String clientIp;

    @CreationTimestamp
    private LocalDateTime receivedAt;


}