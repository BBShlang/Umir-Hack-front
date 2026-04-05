package com.example.verificationservice.kafka.config;

import com.example.verificationservice.kafka.event.CertificateIssuedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, CertificateIssuedEvent> certificateIssuedEventConsumerFactory(
            KafkaProperties kafkaProperties
    ) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "com.example.verificationservice.kafka.event");
        props.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE, CertificateIssuedEvent.class.getName());

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JacksonJsonDeserializer<>(CertificateIssuedEvent.class, false)
        );
    }
}