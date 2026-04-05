package com.example.issuerservice.crypto.service;

import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

@Service
public class PayloadKeyService {

    public String generateBase64AesKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);

            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate AES key", e);
        }
    }
}