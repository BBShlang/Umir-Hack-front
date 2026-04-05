package com.example.verificationservice.verification.dto;

import java.util.UUID;

public record VerifiedCertificateDto(
        UUID certificateId,
        String diplomaNumber,
        String universityCode,
        String universityName,
        String fullNameMasked,
        String specialty,
        Integer graduationYear
) {
}