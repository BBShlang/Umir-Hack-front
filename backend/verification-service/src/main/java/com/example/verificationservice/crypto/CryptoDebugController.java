package com.example.verificationservice.crypto;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test/crypto")
@RequiredArgsConstructor
public class CryptoDebugController {

    private final PayloadDecryptionService payloadDecryptionService;

    @PostMapping("/decrypt")
    public String decrypt(@RequestBody String encryptedPayload) {
        return payloadDecryptionService.decrypt(encryptedPayload);
    }
}