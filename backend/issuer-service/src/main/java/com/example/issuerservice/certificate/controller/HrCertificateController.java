package com.example.issuerservice.certificate.controller;

import com.example.issuerservice.certificate.dto.HrCertificateSearchResponse;
import com.example.issuerservice.certificate.service.HrCertificateQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employer/certificates")
@RequiredArgsConstructor
@Tag(name = "Дипломы для HR", description = "Поиск и просмотр дипломов работодателем")
public class HrCertificateController {

    private final HrCertificateQueryService hrCertificateQueryService;

    @Operation(
            summary = "Найти диплом по номеру",
            description = "Позволяет работодателю найти диплом по номеру диплома",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Диплом найден",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = HrCertificateSearchResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Диплом не найден"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав")
            }
    )
    @GetMapping("/search")
    @PreAuthorize("hasRole('EMPLOYER')")
    public HrCertificateSearchResponse findByDiplomaNumber(
            @Parameter(description = "Номер диплома", example = "DIP-2026-001")
            @RequestParam String diplomaNumber
    ) {
        return hrCertificateQueryService.findByDiplomaNumber(diplomaNumber);
    }
}