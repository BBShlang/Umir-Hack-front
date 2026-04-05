package com.example.issuerservice.crypto.controller;

import com.example.issuerservice.crypto.service.CryptoService;
import com.example.issuerservice.crypto.service.HashService;
import com.example.issuerservice.crypto.service.UniversityKeyLookupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test/crypto")
@RequiredArgsConstructor
@Tag(name = "Crypto (Test)", description = "Тестовые операции подписи и верификации")
public class CryptoTestController {

    private final UniversityKeyLookupService universityKeyLookupService;
    private final HashService hashService;
    private final CryptoService cryptoService;

    @Operation(summary = "Подписать payload приватным ключом университета")
    @GetMapping("/sign")
    public Map<String, Object> sign(
            @Parameter(description = "Код университета", example = "MTU-001", required = true)
            @RequestParam String universityCode,

            @Parameter(description = "Произвольный payload", example = "test-diploma-data", required = true)
            @RequestParam String payload
    ) {
        UniversityKeyLookupService.UniversityKeys keys = universityKeyLookupService.getKeysByCode(universityCode);

        String hash = hashService.sha256(payload);
        String signature = cryptoService.sign(hash, keys.privateKey());

        return Map.of(
                "universityCode", universityCode,
                "payload", payload,
                "hash", hash,
                "signature", signature
        );
    }

    @Operation(summary = "Проверить подпись payload по публичному ключу университета")
    @GetMapping("/verify")
    public Map<String, Object> verify(
            @Parameter(description = "Код университета", example = "MTU-001", required = true)
            @RequestParam String universityCode,

            @Parameter(description = "Исходный payload", example = "test-diploma-data", required = true)
            @RequestParam String payload,

            @Parameter(description = "Подпись (base64)", required = true)
            @RequestParam String signature
    ) {
        UniversityKeyLookupService.UniversityKeys keys = universityKeyLookupService.getKeysByCode(universityCode);

        String hash = hashService.sha256(payload);
        boolean valid = cryptoService.verify(hash, signature, keys.publicKey());

        return Map.of(
                "universityCode", universityCode,
                "payload", payload,
                "hash", hash,
                "signatureValid", valid
        );
    }
}