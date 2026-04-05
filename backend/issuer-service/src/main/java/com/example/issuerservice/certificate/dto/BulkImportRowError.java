package com.example.issuerservice.certificate.dto;

public record BulkImportRowError(
        int rowNumber,
        String message
) {
}