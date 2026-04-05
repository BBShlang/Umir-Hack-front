package com.example.issuerservice.certificate.model;

import com.example.issuerservice.student.model.Student;
import com.example.issuerservice.university.model.University;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "diploma_certificates")
public class DiplomaCertificate {

    @Id
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", nullable = false)
    private University university;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(nullable = false)
    private String diplomaNumber;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String encryptedPayload;

    @Column(nullable = false, length = 64)
    private String payloadHash;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String signature;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CertificateStatus status;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;
}