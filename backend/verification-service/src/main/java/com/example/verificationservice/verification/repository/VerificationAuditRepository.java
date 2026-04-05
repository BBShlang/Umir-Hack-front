package com.example.verificationservice.verification.repository;

import com.example.verificationservice.verification.model.VerificationAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VerificationAuditRepository extends JpaRepository<VerificationAudit, UUID> {
}