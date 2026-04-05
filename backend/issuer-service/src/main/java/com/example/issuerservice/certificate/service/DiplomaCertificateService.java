package com.example.issuerservice.certificate.service;

import com.example.issuerservice.certificate.dto.IssueCertificateRequest;
import com.example.issuerservice.certificate.dto.IssueCertificateResponse;
import com.example.issuerservice.certificate.model.CertificateStatus;
import com.example.issuerservice.certificate.model.DiplomaCertificate;
import com.example.issuerservice.certificate.repository.DiplomaCertificateRepository;
import com.example.issuerservice.crypto.service.CryptoService;
import com.example.issuerservice.crypto.service.HashService;
import com.example.issuerservice.crypto.service.PayloadEncryptionService;
import com.example.issuerservice.kafka.CertificateEventProducer;
import com.example.issuerservice.kafka.event.CertificateIssuedEvent;
import com.example.issuerservice.university.model.University;
import com.example.issuerservice.university.repository.UniversityRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DiplomaCertificateService {

    private final DiplomaCertificateRepository certificateRepository;
    private final UniversityRepository universityRepository;
    private final HashService hashService;
    private final CryptoService cryptoService;
    private final PayloadEncryptionService payloadEncryptionService;
    private final ObjectMapper objectMapper;
    private final CertificateEventProducer producer;

    public IssueCertificateResponse issue(IssueCertificateRequest request) {
        University university = universityRepository.findByCode(request.universityCode())
                .orElseThrow(() -> new EntityNotFoundException("University not found"));

        if (certificateRepository.existsByUniversity_CodeAndDiplomaNumber(
                request.universityCode(),
                request.diplomaNumber()
        )) {
            throw new IllegalStateException("Diploma with this number already exists for this university");
        }

        LocalDateTime now = LocalDateTime.now();
        String payload = buildCanonicalPayload(request, university, now);

        String encryptedPayload = payloadEncryptionService.encrypt(payload);
        String payloadHash = hashService.sha256(payload);
        String signature = cryptoService.sign(payloadHash, university.getEncryptedPrivateKey());

        DiplomaCertificate certificate = new DiplomaCertificate();
        certificate.setId(UUID.randomUUID());
        certificate.setUniversity(university);
        certificate.setStudent(null);
        certificate.setDiplomaNumber(request.diplomaNumber());
        certificate.setEncryptedPayload(encryptedPayload);
        certificate.setPayloadHash(payloadHash);
        certificate.setSignature(signature);
        certificate.setStatus(CertificateStatus.ACTIVE);
        certificate.setIssuedAt(now);
        certificate.setExpiresAt(now.plusYears(10));

        certificateRepository.save(certificate);

        producer.sendIssuedEvent(new CertificateIssuedEvent(
                certificate.getId(),
                university.getId(),
                payloadHash,
                signature,
                encryptedPayload,
                null,
                certificate.getStatus().name(),
                certificate.getExpiresAt()
        ));

        return new IssueCertificateResponse(
                certificate.getId(),
                certificate.getDiplomaNumber(),
                certificate.getPayloadHash(),
                certificate.getSignature(),
                certificate.getIssuedAt()
        );
    }

    private String buildCanonicalPayload(
            IssueCertificateRequest request,
            University university,
            LocalDateTime issuedAt
    ) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("universityCode", university.getCode());
            payload.put("diplomaNumber", request.diplomaNumber());
            payload.put("fullName", request.fullName());
            payload.put("specialty", request.specialty());
            payload.put("graduationYear", request.graduationYear());
            payload.put("issuedAt", issuedAt);

            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to build payload", e);
        }
    }
}