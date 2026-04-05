package com.example.issuerservice.student.service;

import com.example.issuerservice.student.dto.StudentProfileResponse;
import com.example.issuerservice.student.model.Student;
import com.example.issuerservice.student.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentProfileService {

    private final StudentRepository studentRepository;

    public StudentProfileResponse getCurrentStudentProfile(String currentUserEmail) {
        Student student = studentRepository.findByUserEmail(currentUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        return new StudentProfileResponse(
                student.getUser().getId(),
                student.getId(),
                student.getUser().getEmail(),
                student.getFullName(),
                student.getBirthDate(),
                student.getCreatedAt()
        );
    }
}