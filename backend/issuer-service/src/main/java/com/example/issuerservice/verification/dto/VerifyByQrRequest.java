package com.example.issuerservice.verification.dto;

import java.util.UUID;

public record VerifyByQrRequest(
        UUID certificateId,
        String token
) {
}