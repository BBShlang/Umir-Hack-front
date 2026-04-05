package com.example.issuerservice.auth.service;

import com.example.issuerservice.auth.dto.*;
import com.example.issuerservice.crypto.service.KeyPairService;
import com.example.issuerservice.employer.model.Employer;
import com.example.issuerservice.employer.repository.EmployerRepository;
import com.example.issuerservice.exception.BusinessConflictException;
import com.example.issuerservice.student.model.Student;
import com.example.issuerservice.student.repository.StudentRepository;
import com.example.issuerservice.university.model.University;
import com.example.issuerservice.university.repository.UniversityRepository;
import com.example.issuerservice.user.model.Role;
import com.example.issuerservice.user.model.User;
import com.example.issuerservice.user.repository.UserRepository;
import com.example.issuerservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final UniversityRepository universityRepository;
    private final StudentRepository studentRepository;
    private final EmployerRepository employerRepository;
    private final PasswordEncoder passwordEncoder;
    private final KeyPairService keyPairService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public RegisterUniversityResponse registerUniversity(RegisterUniversityRequest request) {
        validateEmailNotExists(request.email());

        if (universityRepository.existsByCode(request.universityCode())) {
            throw new BusinessConflictException("University code already exists");
        }

        LocalDateTime now = LocalDateTime.now();

        User user = buildUser(
                request.email(),
                request.password(),
                Role.UNIVERSITY,
                now
        );
        userRepository.save(user);

        KeyPairService.GeneratedKeyPair keyPair = keyPairService.generate();

        University university = new University();
        university.setId(UUID.randomUUID());
        university.setUser(user);
        university.setName(request.universityName());
        university.setCode(request.universityCode());
        university.setPublicKey(keyPair.publicKey());
        university.setEncryptedPrivateKey(keyPair.privateKey());
        university.setCreatedAt(now);

        universityRepository.save(university);

        return new RegisterUniversityResponse(
                user.getId(),
                university.getId(),
                user.getEmail(),
                university.getName(),
                university.getCode(),
                now
        );
    }

    public RegisterStudentResponse registerStudent(RegisterStudentRequest request) {
        validateEmailNotExists(request.email());

        LocalDateTime now = LocalDateTime.now();

        User user = buildUser(
                request.email(),
                request.password(),
                Role.STUDENT,
                now
        );
        userRepository.save(user);

        Student student = new Student();
        student.setId(UUID.randomUUID());
        student.setUser(user);
        student.setFullName(request.fullName());
        student.setBirthDate(request.birthDate());
        student.setCreatedAt(now);

        studentRepository.save(student);

        return new RegisterStudentResponse(
                user.getId(),
                student.getId(),
                user.getEmail(),
                student.getFullName(),
                now
        );
    }

    public RegisterEmployerResponse registerEmployer(RegisterEmployerRequest request) {
        validateEmailNotExists(request.email());

        LocalDateTime now = LocalDateTime.now();

        User user = buildUser(
                request.email(),
                request.password(),
                Role.EMPLOYER,
                now
        );
        userRepository.save(user);

        Employer employer = new Employer();
        employer.setId(UUID.randomUUID());
        employer.setUser(user);
        employer.setCompanyName(request.companyName());
        employer.setCreatedAt(now);

        employerRepository.save(employer);

        return new RegisterEmployerResponse(
                user.getId(),
                employer.getId(),
                user.getEmail(),
                employer.getCompanyName(),
                now
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessConflictException("User not found"));

        String accessToken = jwtService.generateToken(user);
        LocalDateTime issuedAt = LocalDateTime.now();
        LocalDateTime expiresAt = jwtService.extractExpiration(accessToken);

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                accessToken,
                issuedAt,
                expiresAt
        );
    }

    private void validateEmailNotExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessConflictException("Email already exists");
        }
    }

    private User buildUser(String email, String rawPassword, Role role, LocalDateTime now) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setEnabled(true);
        user.setCreatedAt(now);
        return user;
    }
}