ALTER TABLE students
DROP COLUMN IF EXISTS student_number;

ALTER TABLE diploma_certificates
    ALTER COLUMN student_id DROP NOT NULL;

ALTER TABLE diploma_certificates
DROP CONSTRAINT IF EXISTS diploma_certificates_diploma_number_key;

ALTER TABLE diploma_certificates
    ADD CONSTRAINT uq_diploma_certificates_university_diploma
        UNIQUE (university_id, diploma_number);

CREATE TABLE diploma_claim_requests (
                                        id UUID PRIMARY KEY,
                                        certificate_id UUID NOT NULL,
                                        student_id UUID NOT NULL,
                                        status VARCHAR(30) NOT NULL,
                                        created_at TIMESTAMP NOT NULL,
                                        reviewed_at TIMESTAMP NULL,
                                        review_comment TEXT NULL,
                                        CONSTRAINT fk_claim_request_certificate
                                            FOREIGN KEY (certificate_id) REFERENCES diploma_certificates(id),
                                        CONSTRAINT fk_claim_request_student
                                            FOREIGN KEY (student_id) REFERENCES students(id)
);