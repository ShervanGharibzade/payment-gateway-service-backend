package com.example.PGS.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "callback_nonces",
        indexes = @Index(name = "idx_nonce", columnList = "nonce", unique = true))
public class CallbackNonce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nonce;

    @CreationTimestamp
    @Column(nullable = false, updatable = false,name = "created_at")
    private LocalDateTime createdAt;
}
