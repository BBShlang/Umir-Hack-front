package com.example.issuerservice.certificate.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PublicCertificateSearchResponse(
        UUID certificateId,
        String diplomaNumber,
        String maskedFullName,
        String universityName,
        String universityCode,
        String specialty,
        Integer graduationYear,
        String status,
        LocalDateTime issuedAt,
        LocalDateTime expiresAt
) {
}