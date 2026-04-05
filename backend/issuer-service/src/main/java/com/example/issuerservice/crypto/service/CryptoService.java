package com.example.issuerservice.crypto.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class CryptoService {

    private static final String KEY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    public String sign(String data, String base64PrivateKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey privateKey = keyFactory.generatePrivate(spec);

            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));

            byte[] signedBytes = signature.sign();
            return Base64.getEncoder().encodeToString(signedBytes);
        } catch (Exception e) {
            throw new IllegalStateException("Signing failed", e);
        }
    }

    public boolean verify(String data, String base64Signature, String base64PublicKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            PublicKey publicKey = keyFactory.generatePublic(spec);

            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));

            byte[] signatureBytes = Base64.getDecoder().decode(base64Signature);
            return signature.verify(signatureBytes);
        } catch (Exception e) {
            throw new IllegalStateException("Verification failed", e);
        }
    }
}