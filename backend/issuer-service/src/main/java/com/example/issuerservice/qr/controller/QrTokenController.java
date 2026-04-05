package com.example.issuerservice.qr.controller;

import com.example.issuerservice.qr.dto.GenerateCertificateQrResponse;
import com.example.issuerservice.qr.service.QrTokenService;
import com.example.issuerservice.user.model.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
@Tag(name = "QR-коды", description = "Генерация и управление QR-кодами дипломов")
public class QrTokenController {

    private final QrTokenService qrTokenService;

    @Operation(
            summary = "Сгенерировать QR для диплома",
            description = """
                    Создаёт новый QR-токен для диплома и возвращает qrContent,
                    который фронт должен передать в QrcodeVue как value.
                    Для студента генерация доступна только после одобрения привязки диплома.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "QR успешно создан",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = GenerateCertificateQrResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Диплом не найден"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
                    @ApiResponse(responseCode = "409", description = "Диплом ещё не привязан к студенту")
            }
    )
    @PostMapping("/certificates/{certificateId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('UNIVERSITY', 'STUDENT')")
    public GenerateCertificateQrResponse generateForCertificate(
            @Parameter(description = "ID диплома")
            @PathVariable UUID certificateId,
            Authentication authentication
    ) {
        String currentUserEmail = authentication.getName();

        Role currentUserRole = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .map(authority -> authority.replace("ROLE_", ""))
                .map(Role::valueOf)
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("User role not found"));

        return qrTokenService.generateForCertificate(
                certificateId,
                currentUserEmail,
                currentUserRole
        );
    }
}