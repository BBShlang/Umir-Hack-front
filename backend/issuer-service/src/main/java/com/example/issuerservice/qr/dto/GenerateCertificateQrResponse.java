package com.example.issuerservice.qr.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record GenerateCertificateQrResponse(

        @Schema(description = "ID QR-записи")
        UUID qrId,

        @Schema(description = "ID диплома")
        UUID certificateId,

        @Schema(description = "Сгенерированный токен")
        String token,

        @Schema(description = "Содержимое для генерации QR на фронте", example = "http://localhost:8082/api/public/verify?certificateId=123&token=abc")
        String qrContent,

        @Schema(description = "Срок действия QR")
        LocalDateTime expiresAt
) {
}