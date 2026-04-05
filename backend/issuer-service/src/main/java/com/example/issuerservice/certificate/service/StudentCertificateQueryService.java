package com.example.issuerservice.certificate.service;

import com.example.issuerservice.certificate.dto.CertificatePayloadView;
import com.example.issuerservice.certificate.dto.StudentCertificateDetailsResponse;
import com.example.issuerservice.certificate.dto.StudentCertificateItemResponse;
import com.example.issuerservice.certificate.model.DiplomaCertificate;
import com.example.issuerservice.certificate.repository.DiplomaCertificateRepository;
import com.example.issuerservice.crypto.service.PayloadEncryptionService;
import com.example.issuerservice.student.model.Student;
import com.example.issuerservice.student.repository.StudentRepository;
import com.example.issuerservice.user.model.User;
import com.example.issuerservice.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentCertificateQueryService {

    private final DiplomaCertificateRepository certificateRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final PayloadEncryptionService payloadEncryptionService;
    private final ObjectMapper objectMapper;

    public List<StudentCertificateItemResponse> getMyCertificates(String userEmail) {
        Student student = getCurrentStudent(userEmail);

        return certificateRepository.findAllByStudentIdOrderByIssuedAtDesc(student.getId())
                .stream()
                .map(this::toStudentCertificateItemResponse)
                .toList();
    }

    public StudentCertificateDetailsResponse getMyCertificateById(String userEmail, UUID certificateId) {
        Student student = getCurrentStudent(userEmail);

        DiplomaCertificate certificate = certificateRepository.findByIdAndStudentId(certificateId, student.getId())
                .orElseThrow(() -> new EntityNotFoundException("Certificate not found"));

        CertificatePayloadView payload = parsePayload(certificate.getEncryptedPayload());

        return new StudentCertificateDetailsResponse(
                certificate.getId(),
                certificate.getDiplomaNumber(),
                payload.fullName(),
                certificate.getUniversity().getName(),
                certificate.getUniversity().getCode(),
                payload.specialty(),
                payload.graduationYear(),
                certificate.getPayloadHash(),
                certificate.getSignature(),
                certificate.getStatus().name(),
                certificate.getIssuedAt(),
                certificate.getExpiresAt()
        );
    }

    private Student getCurrentStudent(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Student profile not found"));
    }

    private StudentCertificateItemResponse toStudentCertificateItemResponse(DiplomaCertificate certificate) {
        CertificatePayloadView payload = parsePayload(certificate.getEncryptedPayload());

        return new StudentCertificateItemResponse(
                certificate.getId(),
                certificate.getDiplomaNumber(),
                certificate.getUniversity().getName(),
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
}