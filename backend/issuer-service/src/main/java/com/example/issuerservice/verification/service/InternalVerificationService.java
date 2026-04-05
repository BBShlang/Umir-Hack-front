package com.example.issuerservice.verification.service;

import com.example.issuerservice.certificate.model.DiplomaCertificate;
import com.example.issuerservice.certificate.repository.DiplomaCertificateRepository;
import com.example.issuerservice.qr.model.CertificateQrToken;
import com.example.issuerservice.qr.repository.CertificateQrTokenRepository;
import com.example.issuerservice.university.model.University;
import com.example.issuerservice.university.repository.UniversityRepository;
import com.example.issuerservice.verification.dto.IssuerVerificationBundleResponse;
import com.example.issuerservice.verification.dto.VerifyByQrRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InternalVerificationService {

    private final DiplomaCertificateRepository diplomaCertificateRepository;
    private final CertificateQrTokenRepository certificateQrTokenRepository;
    private final UniversityRepository universityRepository;

    public IssuerVerificationBundleResponse resolve(VerifyByQrRequest request) {
        DiplomaCertificate certificate = diplomaCertificateRepository.findById(request.certificateId())
                .orElseThrow(() -> new EntityNotFoundException("Certificate not found"));

        CertificateQrToken qrToken = certificateQrTokenRepository.findByToken(request.token())
                .orElseThrow(() -> new EntityNotFoundException("QR token not found"));

        if (!qrToken.getCertificate().getId().equals(certificate.getId())) {
            throw new IllegalArgumentException("QR token does not belong to the certificate");
        }

        University university = universityRepository.findById(certificate.getUniversity().getId())
                .orElseThrow(() -> new EntityNotFoundException("University not found"));

        return new IssuerVerificationBundleResponse(
                certificate.getId(),
                certificate.getDiplomaNumber(),
                university.getCode(),
                university.getName(),
                university.getPublicKey(),
                qrToken.getStatus().name(),
                qrToken.getExpiresAt(),
                qrToken.getRevokedAt()
        );
    }
}