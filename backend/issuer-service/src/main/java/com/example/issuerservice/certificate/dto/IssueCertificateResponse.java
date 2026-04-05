package com.example.issuerservice.certificate.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record IssueCertificateResponse(

        @Schema(description = "ID диплома")
        UUID certificateId,

        @Schema(description = "Номер диплома", example = "DIP-2026-001")
        String diplomaNumber,

        @Schema(description = "SHA-256 хеш payload")
        String payloadHash,

        @Schema(description = "Цифровая подпись диплома")
        String signature,

        @Schema(description = "Дата выпуска")
        LocalDateTime issuedAt

) {
}