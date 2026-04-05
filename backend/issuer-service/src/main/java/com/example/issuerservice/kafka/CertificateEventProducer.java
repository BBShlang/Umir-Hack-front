package com.example.issuerservice.kafka;

import com.example.issuerservice.kafka.event.CertificateIssuedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CertificateEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "certificate.issued";

    public void sendIssuedEvent(CertificateIssuedEvent event) {
        kafkaTemplate.send(TOPIC, event.certificateId().toString(), event);
    }
}