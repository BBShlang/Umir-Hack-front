package com.example.issuerservice.student.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record StudentProfileResponse(
        UUID userId,
        UUID studentId,
        String email,
        String fullName,
        LocalDate birthDate,
        LocalDateTime createdAt
) {
}