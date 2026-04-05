CREATE TABLE employers (
                           id UUID PRIMARY KEY,
                           user_id UUID NOT NULL UNIQUE,
                           company_name VARCHAR(255) NOT NULL,
                           created_at TIMESTAMP NOT NULL,
                           CONSTRAINT fk_employers_user FOREIGN KEY (user_id) REFERENCES users(id)
);