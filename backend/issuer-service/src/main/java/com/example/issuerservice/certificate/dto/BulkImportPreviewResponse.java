package com.example.issuerservice.certificate.dto;

import java.util.List;

public record BulkImportPreviewResponse(
        int totalRows,
        int validRows,
        int invalidRows,
        List<BulkIssueCertificateRow> validItems,
        List<BulkImportRowError> errors
) {
}