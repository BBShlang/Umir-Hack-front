package com.example.verificationservice.verification.controller;

import com.example.verificationservice.verification.dto.VerifyByQrRequest;
import com.example.verificationservice.verification.dto.VerifyResponse;
import com.example.verificationservice.verification.service.VerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/verifications")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    @PostMapping("/qr")
    @ResponseStatus(HttpStatus.OK)
    public VerifyResponse verifyByQr(@RequestBody @Valid VerifyByQrRequest request) {
        return verificationService.verifyByQr(request);
    }
}