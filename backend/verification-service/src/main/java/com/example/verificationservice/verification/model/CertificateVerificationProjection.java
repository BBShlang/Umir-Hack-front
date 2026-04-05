package com.example.verificationservice.verification.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "certificate_verification_projection")
public class CertificateVerificationProjection {

    @Id
    private UUID certificateId;

    @Column(name = "university_id", nullable = false)
    private UUID universityId;

    @Column(name = "payload_hash", nullable = false, length = 64)
    private String payloadHash;

    @Column(name = "signature", nullable = false, columnDefinition = "TEXT")
    private String signature;

    @Column(name = "encrypted_payload", nullable = false, columnDefinition = "TEXT")
    private String encryptedPayload;

    @Column(name = "certificate_status", nullable = false, length = 50)
    private String certificateStatus;

    @Column(name = "certificate_expires_at", nullable = false)
    private LocalDateTime certificateExpiresAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}