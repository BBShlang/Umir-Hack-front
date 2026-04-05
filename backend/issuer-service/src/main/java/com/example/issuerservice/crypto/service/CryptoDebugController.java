package com.example.issuerservice.crypto.service;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test/crypto")
@RequiredArgsConstructor
public class CryptoDebugController {

    private final PayloadEncryptionService payloadEncryptionService;

    @PostMapping("/decrypt")
    public String decrypt(@RequestBody String encryptedPayload) {
        return payloadEncryptionService.decrypt(encryptedPayload);
    }
}