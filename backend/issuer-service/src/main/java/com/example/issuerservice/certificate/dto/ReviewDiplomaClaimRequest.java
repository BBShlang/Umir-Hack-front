package com.example.issuerservice.certificate.dto;

public record ReviewDiplomaClaimRequest(
        boolean approve,
        String reviewComment
) {
}