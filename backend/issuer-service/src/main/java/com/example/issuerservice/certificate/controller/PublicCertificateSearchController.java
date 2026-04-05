package com.example.issuerservice.certificate.controller;

import com.example.issuerservice.certificate.dto.HrCertificateSearchResponse;
import com.example.issuerservice.certificate.dto.PublicCertificateSearchResponse;
import com.example.issuerservice.certificate.service.HrCertificateQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/certificates")
@RequiredArgsConstructor
@Tag(name = "Публичный поиск дипломов", description = "Поиск диплома без регистрации")
public class PublicCertificateSearchController {

    private final HrCertificateQueryService hrCertificateQueryService;

    @Operation(
            summary = "Найти диплом по номеру без регистрации",
            description = "Позволяет найти диплом по номеру без авторизации в системе",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Диплом найден",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = HrCertificateSearchResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Диплом не найден")
            }
    )
    @GetMapping("/search")
    public PublicCertificateSearchResponse findByDiplomaNumber(
            @RequestParam String diplomaNumber
    ) {
        return hrCertificateQueryService.findPublicByDiplomaNumber(diplomaNumber);
    }
}