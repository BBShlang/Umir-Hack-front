package com.example.issuerservice.certificate.service;

import com.example.issuerservice.certificate.dto.CertificatePayloadView;
import com.example.issuerservice.certificate.dto.HrCertificateSearchResponse;
import com.example.issuerservice.certificate.dto.PublicCertificateSearchResponse;
import com.example.issuerservice.certificate.model.DiplomaCertificate;
import com.example.issuerservice.certificate.repository.DiplomaCertificateRepository;
import com.example.issuerservice.crypto.service.PayloadEncryptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HrCertificateQueryService {

    private final DiplomaCertificateRepository certificateRepository;
    private final PayloadEncryptionService payloadEncryptionService;
    private final ObjectMapper objectMapper;

    public HrCertificateSearchResponse findByDiplomaNumberAndUniversityCode(String diplomaNumber, String universityCode) {
        DiplomaCertificate certificate = certificateRepository
                .findByUniversity_CodeAndDiplomaNumber(universityCode, diplomaNumber)
                .orElseThrow(() -> new EntityNotFoundException("Certificate not found"));

        CertificatePayloadView payload = parsePayload(certificate.getEncryptedPayload());

        return new HrCertificateSearchResponse(
                certificate.getId(),
                certificate.getDiplomaNumber(),
                payload.fullName(),
                certificate.getUniversity().getName(),
                certificate.getUniversity().getCode(),
                payload.specialty(),
                payload.graduationYear(),
                certificate.getStatus().name(),
                certificate.getIssuedAt(),
                certificate.getExpiresAt()
        );
    }

    public PublicCertificateSearchResponse findPublicByDiplomaNumberAndUniversityCode(String diplomaNumber, String universityCode) {
        DiplomaCertificate certificate = certificateRepository
                .findByUniversity_CodeAndDiplomaNumber(universityCode, diplomaNumber)
                .orElseThrow(() -> new EntityNotFoundException("Certificate not found"));

        CertificatePayloadView payload = parsePayload(certificate.getEncryptedPayload());

        return new PublicCertificateSearchResponse(
                certificate.getId(),
                certificate.getDiplomaNumber(),
                maskFullName(payload.fullName()),
                certificate.getUniversity().getName(),
                certificate.getUniversity().getCode(),
                payload.specialty(),
                payload.graduationYear(),
                certificate.getStatus().name(),
                certificate.getIssuedAt(),
                certificate.getExpiresAt()
        );
    }

    private CertificatePayloadView parsePayload(String encryptedPayload) {
        try {
            String payload = payloadEncryptionService.decrypt(encryptedPayload);
            return objectMapper.readValue(payload, CertificatePayloadView.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse certificate payload", e);
        }
    }

    private String maskFullName(String fullName) {
        String[] parts = fullName.split(" ");

        if (parts.length < 2) {
            return fullName;
        }

        String lastName = parts[0];
        String firstInitial = parts[1].charAt(0) + ".";

        String middleInitial = "";
        if (parts.length > 2) {
            middleInitial = parts[2].charAt(0) + ".";
        }

        return lastName + " " + firstInitial + middleInitial;
    }
    public HrCertificateSearchResponse findByDiplomaNumber(String diplomaNumber) {
        DiplomaCertificate certificate = certificateRepository.findByDiplomaNumber(diplomaNumber)
                .orElseThrow(() -> new EntityNotFoundException("Certificate not found"));

        CertificatePayloadView payload = parsePayload(certificate.getEncryptedPayload());

        return new HrCertificateSearchResponse(
                certificate.getId(),
                certificate.getDiplomaNumber(),
                payload.fullName(),
                certificate.getUniversity().getName(),
                certificate.getUniversity().getCode(),
                payload.specialty(),
                payload.graduationYear(),
                certificate.getStatus().name(),
                certificate.getIssuedAt(),
                certificate.getExpiresAt()
        );
    }
    public PublicCertificateSearchResponse findPublicByDiplomaNumber(String diplomaNumber) {
        DiplomaCertificate certificate = certificateRepository.findByDiplomaNumber(diplomaNumber)
                .orElseThrow(() -> new EntityNotFoundException("Certificate not found"));

        CertificatePayloadView payload = parsePayload(certificate.getEncryptedPayload());

        return new PublicCertificateSearchResponse(
                certificate.getId(),
                certificate.getDiplomaNumber(),
                maskFullName(payload.fullName()),
                certificate.getUniversity().getName(),
                certificate.getUniversity().getCode(),
                payload.specialty(),
                payload.graduationYear(),
                certificate.getStatus().name(),
                certificate.getIssuedAt(),
                certificate.getExpiresAt()
        );
    }
}