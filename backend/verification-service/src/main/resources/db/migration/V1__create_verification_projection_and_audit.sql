CREATE TABLE certificate_verification_projection (
                                                     certificate_id UUID PRIMARY KEY,
                                                     university_id UUID NOT NULL,
                                                     payload_hash VARCHAR(64) NOT NULL,
                                                     signature TEXT NOT NULL,
                                                     encrypted_payload TEXT NOT NULL,
                                                     certificate_status VARCHAR(50) NOT NULL,
                                                     certificate_expires_at TIMESTAMP NOT NULL,
                                                     updated_at TIMESTAMP NOT NULL
);

CREATE TABLE verification_audit (
                                    id UUID PRIMARY KEY,
                                    certificate_id UUID NOT NULL,
                                    verification_status VARCHAR(20) NOT NULL,
                                    reason_code VARCHAR(100) NOT NULL,
                                    verified_at TIMESTAMP NOT NULL
);