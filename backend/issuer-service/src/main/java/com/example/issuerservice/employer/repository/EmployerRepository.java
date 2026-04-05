package com.example.issuerservice.employer.repository;

import com.example.issuerservice.employer.model.Employer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmployerRepository extends JpaRepository<Employer, UUID> {
}