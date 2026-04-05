package com.example.issuerservice.certificate.dto;

public record BulkIssueCertificateRow(
        int rowNumber,
        String universityCode,
        String diplomaNumber,
        String fullName,
        String specialty,
        Integer graduationYear
) {
}