package com.example.issuerservice.certificate.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record DiplomaClaimRequestResponse(
        UUID requestId,
        UUID certificateId,
        UUID studentId,
        String status,
        LocalDateTime createdAt,
        LocalDateTime reviewedAt,
        String reviewComment
) {
}