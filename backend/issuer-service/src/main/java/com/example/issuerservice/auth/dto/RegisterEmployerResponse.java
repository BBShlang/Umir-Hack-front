package com.example.issuerservice.auth.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RegisterEmployerResponse(
        UUID userId,
        UUID employerId,
        String email,
        String companyName,
        LocalDateTime createdAt
) {
}