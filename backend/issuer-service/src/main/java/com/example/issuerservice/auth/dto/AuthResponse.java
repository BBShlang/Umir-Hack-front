package com.example.issuerservice.auth.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuthResponse(
        UUID userId,
        String email,
        String role,
        String accessToken,
        LocalDateTime issuedAt,
        LocalDateTime expiresAt
) {
}