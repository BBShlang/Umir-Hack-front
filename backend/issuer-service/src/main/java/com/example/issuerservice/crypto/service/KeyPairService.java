package com.example.issuerservice.crypto.service;

import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

@Service
public class KeyPairService {

    public GeneratedKeyPair generate() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);

            KeyPair keyPair = generator.generateKeyPair();

            String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

            return new GeneratedKeyPair(publicKey, privateKey);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate RSA key pair", e);
        }
    }

    public record GeneratedKeyPair(
            String publicKey,
            String privateKey
    ) {
    }
}