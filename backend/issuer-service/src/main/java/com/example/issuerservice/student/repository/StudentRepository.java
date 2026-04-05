package com.example.issuerservice.student.repository;

import com.example.issuerservice.student.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, UUID> {

    Optional<Student> findByUserEmail(String email);

    // временно оставляем для старого кода, который ищет через userId
    Optional<Student> findByUserId(UUID userId);
}