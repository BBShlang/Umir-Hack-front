CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL,
                       enabled BOOLEAN NOT NULL,
                       created_at TIMESTAMP NOT NULL
);

CREATE TABLE universities (
                              id UUID PRIMARY KEY,
                              user_id UUID NOT NULL UNIQUE,
                              name VARCHAR(255) NOT NULL,
                              code VARCHAR(100) NOT NULL UNIQUE,
                              public_key TEXT NOT NULL,
                              encrypted_private_key TEXT NOT NULL,
                              created_at TIMESTAMP NOT NULL,
                              CONSTRAINT fk_university_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE students (
                          id UUID PRIMARY KEY,
                          user_id UUID NOT NULL UNIQUE,
                          full_name VARCHAR(255) NOT NULL,
                          birth_date DATE NOT NULL,
                          student_number VARCHAR(100) NOT NULL UNIQUE,
                          created_at TIMESTAMP NOT NULL,
                          CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE diploma_certificates (
                                      id UUID PRIMARY KEY,
                                      university_id UUID NOT NULL,
                                      student_id UUID NOT NULL,
                                      diploma_number VARCHAR(100) NOT NULL UNIQUE,
                                      encrypted_payload TEXT NOT NULL,
                                      payload_hash VARCHAR(64) NOT NULL,
                                      signature TEXT NOT NULL,
                                      qr_token VARCHAR(255) NOT NULL UNIQUE,
                                      status VARCHAR(50) NOT NULL,
                                      issued_at TIMESTAMP NOT NULL,
                                      expires_at TIMESTAMP NOT NULL,
                                      CONSTRAINT fk_certificate_university FOREIGN KEY (university_id) REFERENCES universities(id),
                                      CONSTRAINT fk_certificate_student FOREIGN KEY (student_id) REFERENCES students(id)
);