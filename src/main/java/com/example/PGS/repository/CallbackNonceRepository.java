package com.example.PGS.repository;

import com.example.PGS.entity.CallbackNonce;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CallbackNonceRepository
        extends JpaRepository<CallbackNonce, Long> {

    boolean existsByNonce(String nonce);
}
