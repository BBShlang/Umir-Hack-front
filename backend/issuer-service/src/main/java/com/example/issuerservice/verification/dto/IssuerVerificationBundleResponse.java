package com.example.issuerservice.verification.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record IssuerVerificationBundleResponse(
        UUID certificateId,
        String diplomaNumber,
        String universityCode,
        String universityName,
        String publicKey,
        String qrTokenStatus,
        LocalDateTime qrTokenExpiresAt,
        LocalDateTime qrTokenRevokedAt
) {
}