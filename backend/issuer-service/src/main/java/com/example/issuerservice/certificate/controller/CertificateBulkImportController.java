package com.example.issuerservice.certificate.controller;

import com.example.issuerservice.certificate.dto.BulkImportCommitResponse;
import com.example.issuerservice.certificate.dto.BulkImportPreviewResponse;
import com.example.issuerservice.certificate.dto.CsvUploadRequest;
import com.example.issuerservice.certificate.service.CertificateBulkImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/university/certificates/import")
@RequiredArgsConstructor
@Tag(name = "Импорт дипломов", description = "Массовая загрузка дипломов CSV")
public class CertificateBulkImportController {

    private final CertificateBulkImportService certificateBulkImportService;

    @PostMapping(
            value = "/preview",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasRole('UNIVERSITY')")
    @Operation(summary = "Предпросмотр CSV импорта дипломов")
    public BulkImportPreviewResponse preview(@ModelAttribute CsvUploadRequest request) {
        return certificateBulkImportService.preview(request.getFile());
    }

    @PostMapping(
            value = "/commit",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('UNIVERSITY')")
    @Operation(summary = "Подтвердить импорт дипломов из CSV")
    public BulkImportCommitResponse commit(@ModelAttribute CsvUploadRequest request) {
        return certificateBulkImportService.commit(request.getFile());
    }
}