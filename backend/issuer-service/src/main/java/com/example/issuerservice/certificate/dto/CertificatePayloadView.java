package com.example.issuerservice.certificate.dto;

import java.time.LocalDateTime;

public record CertificatePayloadView(
        String universityCode,
        String diplomaNumber,
        String fullName,
        String specialty,
        Integer graduationYear,
        LocalDateTime issuedAt
) {
}