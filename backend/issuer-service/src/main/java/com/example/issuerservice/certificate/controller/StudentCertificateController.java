package com.example.issuerservice.certificate.controller;

import com.example.issuerservice.certificate.dto.StudentCertificateDetailsResponse;
import com.example.issuerservice.certificate.dto.StudentCertificateItemResponse;
import com.example.issuerservice.certificate.service.StudentCertificateQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/certificates/my")
@RequiredArgsConstructor
@Tag(name = "Дипломы студента", description = "Просмотр дипломов в личном кабинете студента")
public class StudentCertificateController {

    private final StudentCertificateQueryService studentCertificateQueryService;

    @Operation(
            summary = "Получить мои дипломы",
            description = "Возвращает список дипломов текущего авторизованного студента",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список дипломов успешно получен",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = StudentCertificateItemResponse.class))
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав")
            }
    )
    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public List<StudentCertificateItemResponse> getMyCertificates(Authentication authentication) {
        return studentCertificateQueryService.getMyCertificates(authentication.getName());
    }

    @Operation(
            summary = "Получить мой диплом по ID",
            description = "Возвращает подробную информацию о дипломе текущего студента",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Диплом успешно найден",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StudentCertificateDetailsResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Диплом не найден"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав")
            }
    )
    @GetMapping("/{certificateId}")
    @PreAuthorize("hasRole('STUDENT')")
    public StudentCertificateDetailsResponse getMyCertificateById(
            @PathVariable UUID certificateId,
            Authentication authentication
    ) {
        return studentCertificateQueryService.getMyCertificateById(authentication.getName(), certificateId);
    }
}