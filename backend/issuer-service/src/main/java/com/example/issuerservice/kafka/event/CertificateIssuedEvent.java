package com.example.issuerservice.kafka.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record CertificateIssuedEvent(
        UUID certificateId,
        UUID universityId,
        String payloadHash,
        String signature,
        String encryptedPayload,
        String qrToken,
        String status,
        LocalDateTime expiresAt
) {
}