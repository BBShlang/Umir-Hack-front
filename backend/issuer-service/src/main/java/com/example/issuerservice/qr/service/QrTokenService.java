package com.example.issuerservice.qr.service;

import com.example.issuerservice.certificate.model.DiplomaCertificate;
import com.example.issuerservice.certificate.repository.DiplomaCertificateRepository;
import com.example.issuerservice.exception.BusinessConflictException;
import com.example.issuerservice.qr.dto.GenerateCertificateQrResponse;
import com.example.issuerservice.qr.model.CertificateQrToken;
import com.example.issuerservice.qr.model.QrTokenStatus;
import com.example.issuerservice.qr.repository.CertificateQrTokenRepository;
import com.example.issuerservice.user.model.Role;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class QrTokenService {

    private final CertificateQrTokenRepository qrTokenRepository;
    private final DiplomaCertificateRepository certificateRepository;

    @Value("${app.qr.base-url}")
    private String qrBaseUrl;

    @Value("${app.qr.ttl-minutes:1440}")
    private long qrTtlMinutes;

    public GenerateCertificateQrResponse generateForCertificate(
            UUID certificateId,
            String currentUserEmail,
            Role currentUserRole
    ) {
        DiplomaCertificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new EntityNotFoundException("Diploma certificate not found"));

        validateAccess(certificate, currentUserEmail, currentUserRole);

        qrTokenRepository.findByCertificateIdAndStatus(certificateId, QrTokenStatus.ACTIVE)
                .ifPresent(existing -> {
                    existing.setStatus(QrTokenStatus.REVOKED);
                    existing.setRevokedAt(LocalDateTime.now());
                });

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(qrTtlMinutes);
        String token = UUID.randomUUID().toString();

        CertificateQrToken qrToken = new CertificateQrToken();
        qrToken.setId(UUID.randomUUID());
        qrToken.setCertificate(certificate);
        qrToken.setToken(token);
        qrToken.setStatus(QrTokenStatus.ACTIVE);
        qrToken.setCreatedAt(now);
        qrToken.setExpiresAt(expiresAt);
        qrToken.setRevokedAt(null);

        qrTokenRepository.save(qrToken);

        String qrContent = qrBaseUrl
                + "?certificateId=" + certificate.getId()
                + "&token=" + token;

        return new GenerateCertificateQrResponse(
                qrToken.getId(),
                certificate.getId(),
                token,
                qrContent,
                expiresAt
        );
    }

    private void validateAccess(
            DiplomaCertificate certificate,
            String currentUserEmail,
            Role currentUserRole
    ) {
        if (currentUserRole == Role.UNIVERSITY) {
            validateUniversityAccess(certificate, currentUserEmail);
            return;
        }

        if (currentUserRole == Role.STUDENT) {
            validateStudentAccess(certificate, currentUserEmail);
            return;
        }

        throw new AccessDeniedException("QR generation is not allowed for this role");
    }

    private void validateUniversityAccess(DiplomaCertificate certificate, String currentUserEmail) {
        if (certificate.getUniversity() == null
                || certificate.getUniversity().getUser() == null
                || certificate.getUniversity().getUser().getEmail() == null
                || !certificate.getUniversity().getUser().getEmail().equals(currentUserEmail)) {
            throw new AccessDeniedException("You cannot generate QR for a diploma of another university");
        }
    }

    private void validateStudentAccess(DiplomaCertificate certificate, String currentUserEmail) {
        if (certificate.getStudent() == null) {
            throw new BusinessConflictException("Diploma is not linked to a student yet");
        }

        if (certificate.getStudent().getUser() == null
                || certificate.getStudent().getUser().getEmail() == null
                || !certificate.getStudent().getUser().getEmail().equals(currentUserEmail)) {
            throw new AccessDeniedException("You cannot generate QR for someone else's diploma");
        }
    }
}