package com.example.verificationservice.verification.dto;

public record VerificationChecksDto(
        String token,
        String ttl,
        String revocation,
        String payloadHash,
        String signature
) {
}