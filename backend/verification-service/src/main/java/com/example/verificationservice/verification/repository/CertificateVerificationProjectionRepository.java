package com.example.verificationservice.verification.repository;

import com.example.verificationservice.verification.model.CertificateVerificationProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CertificateVerificationProjectionRepository
        extends JpaRepository<CertificateVerificationProjection, UUID> {
}