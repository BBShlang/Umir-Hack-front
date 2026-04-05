package com.example.verificationservice.verification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record VerifyByQrRequest(
        @NotNull UUID certificateId,
        @NotBlank String token
) {
}