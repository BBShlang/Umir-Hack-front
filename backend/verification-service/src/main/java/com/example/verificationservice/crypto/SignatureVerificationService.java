package com.example.verificationservice.crypto;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class SignatureVerificationService {

    public boolean verify(String payloadHash, String signatureBase64, String publicKeyBase64) {
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(spec);

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(payloadHash.getBytes(StandardCharsets.UTF_8));

            return signature.verify(Base64.getDecoder().decode(signatureBase64));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to verify signature", e);
        }
    }
}