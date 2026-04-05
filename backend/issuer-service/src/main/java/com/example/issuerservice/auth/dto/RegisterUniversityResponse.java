package com.example.issuerservice.auth.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RegisterUniversityResponse(
        UUID userId,
        UUID universityId,
        String email,
        String universityName,
        String universityCode,
        LocalDateTime createdAt
) {
}