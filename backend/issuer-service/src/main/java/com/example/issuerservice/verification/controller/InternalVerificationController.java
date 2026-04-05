package com.example.issuerservice.verification.controller;

import com.example.issuerservice.verification.dto.IssuerVerificationBundleResponse;
import com.example.issuerservice.verification.dto.VerifyByQrRequest;
import com.example.issuerservice.verification.service.InternalVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/api/v1/verifications")
@RequiredArgsConstructor
public class InternalVerificationController {

    private final InternalVerificationService internalVerificationService;

    @PostMapping("/resolve")
    public IssuerVerificationBundleResponse resolve(@RequestBody VerifyByQrRequest request) {
        return internalVerificationService.resolve(request);
    }
}