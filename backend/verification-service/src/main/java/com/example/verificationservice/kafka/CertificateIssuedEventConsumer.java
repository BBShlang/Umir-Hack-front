package com.example.verificationservice.kafka;

import com.example.verificationservice.kafka.event.CertificateIssuedEvent;
import com.example.verificationservice.verification.model.CertificateVerificationProjection;
import com.example.verificationservice.verification.repository.CertificateVerificationProjectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CertificateIssuedEventConsumer {

    private final CertificateVerificationProjectionRepository projectionRepository;

    @KafkaListener(
            topics = "${app.kafka.topics.certificate-issued:certificate.issued}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void consume(CertificateIssuedEvent event) {
        log.info("Received CertificateIssuedEvent: certificateId={}", event.certificateId());

        CertificateVerificationProjection projection = projectionRepository
                .findById(event.certificateId())
                .orElseGet(CertificateVerificationProjection::new);

        projection.setCertificateId(event.certificateId());
        projection.setUniversityId(event.universityId());
        projection.setPayloadHash(event.payloadHash());
        projection.setSignature(event.signature());
        projection.setEncryptedPayload(event.encryptedPayload());
        projection.setCertificateStatus(event.status());
        projection.setCertificateExpiresAt(event.expiresAt());
        projection.setUpdatedAt(LocalDateTime.now());

        projectionRepository.save(projection);
    }
}