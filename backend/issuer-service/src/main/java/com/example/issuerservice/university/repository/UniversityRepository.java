package com.example.issuerservice.university.repository;

import com.example.issuerservice.university.model.University;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UniversityRepository extends JpaRepository<University, UUID> {
    Optional<University> findByCode(String code);
    boolean existsByCode(String code);
}