CREATE TABLE certificate_qr_tokens (
                                       id UUID PRIMARY KEY,
                                       certificate_id UUID NOT NULL,
                                       token VARCHAR(255) NOT NULL UNIQUE,
                                       status VARCHAR(50) NOT NULL,
                                       created_at TIMESTAMP NOT NULL,
                                       expires_at TIMESTAMP NOT NULL,
                                       revoked_at TIMESTAMP NULL,
                                       CONSTRAINT fk_qr_certificate FOREIGN KEY (certificate_id) REFERENCES diploma_certificates(id)
);