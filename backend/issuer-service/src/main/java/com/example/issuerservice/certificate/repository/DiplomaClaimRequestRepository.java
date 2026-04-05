package com.example.issuerservice.certificate.repository;

import com.example.issuerservice.certificate.model.DiplomaClaimRequest;
import com.example.issuerservice.certificate.model.DiplomaClaimRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DiplomaClaimRequestRepository extends JpaRepository<DiplomaClaimRequest, UUID> {

    boolean existsByCertificate_IdAndStudent_IdAndStatus(
            UUID certificateId,
            UUID studentId,
            DiplomaClaimRequestStatus status
    );

    Optional<DiplomaClaimRequest> findByIdAndCertificate_University_User_Email(
            UUID requestId,
            String universityUserEmail
    );

    List<DiplomaClaimRequest> findAllByCertificate_University_User_EmailAndStatusOrderByCreatedAtAsc(
            String universityUserEmail,
            DiplomaClaimRequestStatus status
    );
}