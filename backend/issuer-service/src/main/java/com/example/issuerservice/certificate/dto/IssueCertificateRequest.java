package com.example.issuerservice.certificate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Запрос на выпуск диплома")
public record IssueCertificateRequest(

        @Schema(example = "AITU-01")
        @NotBlank(message = "University code is required")
        String universityCode,

        @Schema(example = "DIP-2026-001")
        @NotBlank(message = "Diploma number is required")
        String diplomaNumber,

        @Schema(example = "Ivan Ivanov")
        @NotBlank(message = "Full name is required")
        String fullName,

        @Schema(example = "Computer Science")
        @NotBlank(message = "Specialty is required")
        String specialty,

        @Schema(example = "2026")
        @NotNull(message = "Graduation year is required")
        Integer graduationYear
) {
}