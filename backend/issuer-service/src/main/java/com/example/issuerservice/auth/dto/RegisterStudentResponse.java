package com.example.issuerservice.auth.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RegisterStudentResponse(
        UUID userId,
        UUID studentId,
        String email,
        String fullName,
        LocalDateTime createdAt
) {
}