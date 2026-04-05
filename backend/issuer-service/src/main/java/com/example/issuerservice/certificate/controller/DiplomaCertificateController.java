package com.example.issuerservice.certificate.controller;

import com.example.issuerservice.certificate.dto.IssueCertificateRequest;
import com.example.issuerservice.certificate.dto.IssueCertificateResponse;
import com.example.issuerservice.certificate.service.DiplomaCertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
@Tag(name = "Дипломы", description = "Выпуск и управление дипломами")
public class DiplomaCertificateController {

    private final DiplomaCertificateService diplomaCertificateService;

    @Operation(
            summary = "Создать диплом (выпуск)",
            description = "Выпускает диплом: формирует payload, hash, цифровую подпись и сохраняет запись в БД",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Данные для выпуска диплома",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IssueCertificateRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "universityCode": "AITU-01",
                                              "diplomaNumber": "DIP-2026-001",
                                              "fullName": "Ivan Ivanov",
                                              "specialty": "Computer Science",
                                              "graduationYear": 2026
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Диплом успешно выпущен",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = IssueCertificateResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Университет не найден"),
                    @ApiResponse(responseCode = "409", description = "Конфликт данных, например номер диплома уже существует"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав")
            }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('UNIVERSITY')")
    public IssueCertificateResponse issue(@Valid @RequestBody IssueCertificateRequest request) {
        return diplomaCertificateService.issue(request);
    }
}