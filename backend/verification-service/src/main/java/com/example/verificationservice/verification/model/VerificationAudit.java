package com.example.verificationservice.verification.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "verification_audit")
public class VerificationAudit {

    @Id
    private UUID id;

    @Column(name = "certificate_id", nullable = false)
    private UUID certificateId;

    @Column(name = "verification_status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus;

    @Column(name = "reason_code", nullable = false, length = 100)
    @Enumerated(EnumType.STRING)
    private VerificationReasonCode reasonCode;

    @Column(name = "verified_at", nullable = false)
    private LocalDateTime verifiedAt;
}