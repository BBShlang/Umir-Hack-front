package com.example.verificationservice.verification.dto;

import java.time.LocalDateTime;

public record VerifyResponse(
        String verificationId,
        String status,
        String reasonCode,
        LocalDateTime verifiedAt,
        VerifiedCertificateDto certificate,
        VerificationChecksDto checks
) {
}