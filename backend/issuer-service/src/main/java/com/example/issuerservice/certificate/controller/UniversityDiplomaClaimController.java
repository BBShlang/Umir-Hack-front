package com.example.issuerservice.certificate.controller;

import com.example.issuerservice.certificate.dto.DiplomaClaimRequestResponse;
import com.example.issuerservice.certificate.dto.ReviewDiplomaClaimRequest;
import com.example.issuerservice.certificate.service.DiplomaClaimRequestService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/university/diploma-claims")
@RequiredArgsConstructor
public class UniversityDiplomaClaimController {

    private final DiplomaClaimRequestService diplomaClaimRequestService;

    @GetMapping("/pending")
    @Operation(summary = "Получить pending заявки на привязку дипломов")
    public List<DiplomaClaimRequestResponse> getPending(Authentication authentication) {
        return diplomaClaimRequestService.getPendingRequestsForUniversity(authentication.getName());
    }

    @PostMapping("/{requestId}/review")
    @Operation(summary = "Одобрить или отклонить заявку на привязку диплома")
    public DiplomaClaimRequestResponse review(
            @PathVariable UUID requestId,
            @RequestBody @Valid ReviewDiplomaClaimRequest request,
            Authentication authentication
    ) {
        return diplomaClaimRequestService.reviewRequest(requestId, request, authentication.getName());
    }
}