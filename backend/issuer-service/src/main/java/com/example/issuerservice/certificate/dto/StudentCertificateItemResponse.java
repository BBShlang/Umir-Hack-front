package com.example.issuerservice.certificate.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record StudentCertificateItemResponse(

        @Schema(description = "ID диплома")
        UUID certificateId,

        @Schema(description = "Номер диплома", example = "DIP-2026-001")
        String diplomaNumber,

        @Schema(description = "Название университета", example = "Astana IT University")
        String universityName,

        @Schema(description = "Специальность", example = "Computer Science")
        String specialty,

        @Schema(description = "Год выпуска", example = "2026")
        Integer graduationYear,

        @Schema(description = "Статус диплома", example = "ACTIVE")
        String status,

        @Schema(description = "Дата выпуска")
        LocalDateTime issuedAt,

        @Schema(description = "Дата окончания действия")
        LocalDateTime expiresAt
) {
}