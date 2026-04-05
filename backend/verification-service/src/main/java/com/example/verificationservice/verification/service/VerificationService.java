package com.example.verificationservice.verification.service;

import com.example.verificationservice.client.IssuerVerificationClient;
import com.example.verificationservice.client.dto.IssuerVerificationBundleResponse;
import com.example.verificationservice.crypto.HashService;
import com.example.verificationservice.crypto.PayloadDecryptionService;
import com.example.verificationservice.crypto.SignatureVerificationService;
import com.example.verificationservice.verification.dto.*;
import com.example.verificationservice.verification.model.*;
import com.example.verificationservice.verification.repository.CertificateVerificationProjectionRepository;
import com.example.verificationservice.verification.repository.VerificationAuditRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class VerificationService {

    private final CertificateVerificationProjectionRepository projectionRepository;
    private final VerificationAuditRepository auditRepository;
    private final IssuerVerificationClient issuerVerificationClient;
    private final PayloadDecryptionService payloadDecryptionService;
    private final HashService hashService;
    private final SignatureVerificationService signatureVerificationService;
    private final ObjectMapper objectMapper;

    public VerifyResponse verifyByQr(VerifyByQrRequest request) {
        String verificationId = "vf_" + UUID.randomUUID();
        LocalDateTime verifiedAt = LocalDateTime.now();

        CertificateVerificationProjection projection = projectionRepository.findById(request.certificateId())
                .orElseThrow(() -> new EntityNotFoundException("Certificate projection not found"));

        IssuerVerificationBundleResponse bundle = issuerVerificationClient.resolveVerificationBundle(request);

        if (!"ACTIVE".equalsIgnoreCase(projection.getCertificateStatus())) {
            return fail(verificationId, projection.getCertificateId(), VerificationReasonCode.CERTIFICATE_INACTIVE, verifiedAt);
        }

        if (projection.getCertificateExpiresAt().isBefore(verifiedAt)) {
            return fail(verificationId, projection.getCertificateId(), VerificationReasonCode.CERTIFICATE_EXPIRED, verifiedAt);
        }

        if (!"ACTIVE".equalsIgnoreCase(bundle.qrTokenStatus())) {
            return fail(verificationId, projection.getCertificateId(), VerificationReasonCode.QR_TOKEN_REVOKED, verifiedAt);
        }

        if (bundle.qrTokenExpiresAt() != null && bundle.qrTokenExpiresAt().isBefore(verifiedAt)) {
            return fail(verificationId, projection.getCertificateId(), VerificationReasonCode.QR_TOKEN_EXPIRED, verifiedAt);
        }

        if (bundle.qrTokenRevokedAt() != null) {
            return fail(verificationId, projection.getCertificateId(), VerificationReasonCode.QR_TOKEN_REVOKED, verifiedAt);
        }

        String payload;
        try {
            payload = payloadDecryptionService.decrypt(projection.getEncryptedPayload());
        } catch (Exception e) {
            return fail(verificationId, projection.getCertificateId(), VerificationReasonCode.PAYLOAD_DECRYPTION_FAILED, verifiedAt);
        }

        String recalculatedHash = hashService.sha256(payload);
        if (!recalculatedHash.equals(projection.getPayloadHash())) {
            return fail(verificationId, projection.getCertificateId(), VerificationReasonCode.HASH_MISMATCH, verifiedAt);
        }

        if (bundle.publicKey() == null || bundle.publicKey().isBlank()) {
            return fail(verificationId, projection.getCertificateId(), VerificationReasonCode.UNIVERSITY_KEY_NOT_FOUND, verifiedAt);
        }

        boolean signatureValid = signatureVerificationService.verify(
                projection.getPayloadHash(),
                projection.getSignature(),
                bundle.publicKey()
        );

        if (!signatureValid) {
            return fail(verificationId, projection.getCertificateId(), VerificationReasonCode.SIGNATURE_INVALID, verifiedAt);
        }

        DiplomaPayloadView payloadView = readPayload(payload);

        VerifyResponse response = new VerifyResponse(
                verificationId,
                VerificationStatus.VALID.name(),
                VerificationReasonCode.SIGNATURE_VERIFIED.name(),
                verifiedAt,
                new VerifiedCertificateDto(
                        projection.getCertificateId(),
                        bundle.diplomaNumber(),
                        bundle.universityCode(),
                        bundle.universityName(),
                        maskFullName(payloadView.fullName()),
                        payloadView.specialty(),
                        payloadView.graduationYear()
                ),
                new VerificationChecksDto(
                        "PASSED",
                        "PASSED",
                        "PASSED",
                        "PASSED",
                        "PASSED"
                )
        );

        saveAudit(projection.getCertificateId(), VerificationStatus.VALID, VerificationReasonCode.SIGNATURE_VERIFIED, verifiedAt);
        return response;
    }

    private DiplomaPayloadView readPayload(String payload) {
        try {
            return objectMapper.readValue(payload, DiplomaPayloadView.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse payload", e);
        }
    }

    private VerifyResponse fail(
            String verificationId,
            UUID certificateId,
            VerificationReasonCode reasonCode,
            LocalDateTime verifiedAt
    ) {
        saveAudit(certificateId, VerificationStatus.INVALID, reasonCode, verifiedAt);

        return new VerifyResponse(
                verificationId,
                VerificationStatus.INVALID.name(),
                reasonCode.name(),
                verifiedAt,
                null,
                new VerificationChecksDto(
                        failedCheck(reasonCode, "token"),
                        failedCheck(reasonCode, "ttl"),
                        failedCheck(reasonCode, "revocation"),
                        failedCheck(reasonCode, "payloadHash"),
                        failedCheck(reasonCode, "signature")
                )
        );
    }

    private String failedCheck(VerificationReasonCode reasonCode, String checkName) {
        return switch (checkName) {
            case "token" -> reasonCode == VerificationReasonCode.QR_TOKEN_NOT_FOUND ? "FAILED" : "UNKNOWN";
            case "ttl" -> reasonCode == VerificationReasonCode.QR_TOKEN_EXPIRED
                    || reasonCode == VerificationReasonCode.CERTIFICATE_EXPIRED ? "FAILED" : "UNKNOWN";
            case "revocation" -> reasonCode == VerificationReasonCode.QR_TOKEN_REVOKED ? "FAILED" : "UNKNOWN";
            case "payloadHash" -> reasonCode == VerificationReasonCode.HASH_MISMATCH ? "FAILED" : "UNKNOWN";
            case "signature" -> reasonCode == VerificationReasonCode.SIGNATURE_INVALID ? "FAILED" : "UNKNOWN";
            default -> "UNKNOWN";
        };
    }

    private void saveAudit(
            UUID certificateId,
            VerificationStatus status,
            VerificationReasonCode reasonCode,
            LocalDateTime verifiedAt
    ) {
        VerificationAudit audit = new VerificationAudit();
        audit.setId(UUID.randomUUID());
        audit.setCertificateId(certificateId);
        audit.setVerificationStatus(status);
        audit.setReasonCode(reasonCode);
        audit.setVerifiedAt(verifiedAt);

        auditRepository.save(audit);
    }

    private String maskFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return null;
        }

        String[] parts = fullName.trim().split("\\s+");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (!part.isBlank()) {
                result.append(part.charAt(0)).append("***");
                if (i < parts.length - 1) {
                    result.append(" ");
                }
            }
        }

        return result.toString();
    }
}