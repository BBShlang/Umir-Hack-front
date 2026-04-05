package com.example.verificationservice.verification.dto;

import java.time.LocalDateTime;

public record DiplomaPayloadView(
        String universityCode,
        String diplomaNumber,
        String fullName,
        String specialty,
        Integer graduationYear,
        LocalDateTime issuedAt
) {
}