package com.example.issuerservice.certificate.dto;

import java.util.List;

public record BulkImportCommitResponse(
        int totalRows,
        int importedRows,
        int failedRows,
        List<String> importedDiplomaNumbers,
        List<BulkImportRowError> errors
) {
}