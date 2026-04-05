package com.example.issuerservice.qr.repository;

import com.example.issuerservice.qr.model.CertificateQrToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CertificateQrTokenRepository extends JpaRepository<CertificateQrToken, UUID> {

    Optional<CertificateQrToken> findByToken(String token);

    Optional<CertificateQrToken> findByCertificateIdAndStatus(UUID certificateId, com.example.issuerservice.qr.model.QrTokenStatus status);
}