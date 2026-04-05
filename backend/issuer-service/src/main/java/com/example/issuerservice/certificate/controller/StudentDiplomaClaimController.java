package com.example.issuerservice.certificate.controller;

import com.example.issuerservice.certificate.dto.CreateDiplomaClaimRequest;
import com.example.issuerservice.certificate.dto.DiplomaClaimRequestResponse;
import com.example.issuerservice.certificate.service.DiplomaClaimRequestService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/diploma-claims")
@RequiredArgsConstructor
public class StudentDiplomaClaimController {

    private final DiplomaClaimRequestService diplomaClaimRequestService;

    @PostMapping
    @Operation(summary = "Подать заявку на привязку диплома к студенту")
    public DiplomaClaimRequestResponse createClaim(
            @RequestBody @Valid CreateDiplomaClaimRequest request,
            Authentication authentication
    ) {
        return diplomaClaimRequestService.createRequest(request, authentication.getName());
    }
}