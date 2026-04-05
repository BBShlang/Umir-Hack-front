package com.example.verificationservice.crypto;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class PayloadDecryptionService {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_LENGTH = 12;

    @Value("${app.crypto.payload-secret}")
    private String payloadSecretBase64;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        byte[] decodedKey = Base64.getDecoder().decode(payloadSecretBase64);
        this.secretKey = new SecretKeySpec(decodedKey, ALGORITHM);
    }

    public String decrypt(String encryptedPayload) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedPayload);

            ByteBuffer buffer = ByteBuffer.wrap(decoded);
            byte[] iv = new byte[IV_LENGTH];
            buffer.get(iv);

            byte[] cipherText = new byte[buffer.remaining()];
            buffer.get(cipherText);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

            byte[] decryptedBytes = cipher.doFinal(cipherText);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to decrypt payload", e);
        }
    }
}