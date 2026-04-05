package com.example.issuerservice.certificate.repository;

import com.example.issuerservice.certificate.model.DiplomaCertificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DiplomaCertificateRepository extends JpaRepository<DiplomaCertificate, UUID> {

    boolean existsByUniversity_CodeAndDiplomaNumber(String universityCode, String diplomaNumber);

    Optional<DiplomaCertificate> findByUniversity_CodeAndDiplomaNumber(String universityCode, String diplomaNumber);

    // временно оставляем для старого HR/public flow
    Optional<DiplomaCertificate> findByDiplomaNumber(String diplomaNumber);

    // для кабинета студента после одобренной привязки
    List<DiplomaCertificate> findAllByStudentIdOrderByIssuedAtDesc(UUID studentId);

    Optional<DiplomaCertificate> findByIdAndStudentId(UUID certificateId, UUID studentId);

    List<DiplomaCertificate> findAllByStudent_User_Email(String email);
}