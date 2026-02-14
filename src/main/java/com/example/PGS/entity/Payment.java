package com.example.PGS.entity;


import com.example.PGS.entity.enums.PaymentGateway;
import com.example.PGS.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments",
        indexes = {
                @Index(name = "idx_payments_reference_number", columnList = "reference_number"),
                @Index(name = "idx_payments_merchant_id",      columnList = "merchant_id"),
                @Index(name = "idx_payments_status",           columnList = "status"),
                @Index(name = "idx_payments_gateway",          columnList = "gateway"),
                @Index(name = "idx_payments_created_at",       columnList = "created_at")
        })
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Merchant is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false, foreignKey = @ForeignKey(name = "fk_payment_merchant"))
    private Merchant merchant;

    @Column(nullable = false, precision = 18, scale = 2)
    @Positive(message = "Amount must be greater than zero")
    @NotNull(message = "Amount is required")
    @Digits(integer = 16, fraction = 2)
    private BigDecimal amount;

    @NotNull(message = "Payment status is required")
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status;

    @Column(
            name = "reference_number",
            nullable = false,
            unique = true,
            length = 64,
            updatable = false
    )
    @NotBlank(message = "Reference number is required")
    private String referenceNumber;

    @Size(max = 255)
    @Column(length = 255)
    private String description;

    @NotNull(message = "Payment gateway is required")
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private PaymentGateway gateway;

    @Size(max = 100)
    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @Column(name = "callback_received_at")
    private LocalDateTime callbackReceivedAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Size(max = 255)
    @Column(name = "failure_reason", length = 255)
    private String failureReason;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void markAsPaid(String trackingNumber) {
        this.status = PaymentStatus.SUCCESS;
        this.trackingNumber = trackingNumber;
        this.paidAt = LocalDateTime.now();
        this.callbackReceivedAt = LocalDateTime.now();
    }

    public void markAsFailed(String reason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
        this.callbackReceivedAt = LocalDateTime.now();
    }
}