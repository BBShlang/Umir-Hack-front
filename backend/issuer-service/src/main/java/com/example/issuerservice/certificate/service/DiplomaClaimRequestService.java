package com.example.issuerservice.certificate.service;

import com.example.issuerservice.certificate.dto.CreateDiplomaClaimRequest;
import com.example.issuerservice.certificate.dto.DiplomaClaimRequestResponse;
import com.example.issuerservice.certificate.dto.ReviewDiplomaClaimRequest;
import com.example.issuerservice.certificate.model.DiplomaCertificate;
import com.example.issuerservice.certificate.model.DiplomaClaimRequest;
import com.example.issuerservice.certificate.model.DiplomaClaimRequestStatus;
import com.example.issuerservice.certificate.repository.DiplomaCertificateRepository;
import com.example.issuerservice.certificate.repository.DiplomaClaimRequestRepository;
import com.example.issuerservice.exception.BusinessConflictException;
import com.example.issuerservice.student.model.Student;
import com.example.issuerservice.student.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DiplomaClaimRequestService {

    private final DiplomaClaimRequestRepository claimRequestRepository;
    private final DiplomaCertificateRepository certificateRepository;
    private final StudentRepository studentRepository;

    public DiplomaClaimRequestResponse createRequest(CreateDiplomaClaimRequest request, String currentUserEmail) {
        Student student = studentRepository.findByUserEmail(currentUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        DiplomaCertificate certificate = certificateRepository
                .findByUniversity_CodeAndDiplomaNumber(request.universityCode(), request.diplomaNumber())
                .orElseThrow(() -> new EntityNotFoundException("Diploma not found"));

        if (certificate.getStudent() != null) {
            throw new BusinessConflictException("Diploma is already linked to a student");
        }

        if (claimRequestRepository.existsByCertificate_IdAndStudent_IdAndStatus(
                certificate.getId(),
                student.getId(),
                DiplomaClaimRequestStatus.PENDING
        )) {
            throw new BusinessConflictException("Claim request already exists");
        }

        DiplomaClaimRequest claimRequest = new DiplomaClaimRequest();
        claimRequest.setId(UUID.randomUUID());
        claimRequest.setCertificate(certificate);
        claimRequest.setStudent(student);
        claimRequest.setStatus(DiplomaClaimRequestStatus.PENDING);
        claimRequest.setCreatedAt(LocalDateTime.now());

        claimRequestRepository.save(claimRequest);

        return toResponse(claimRequest);
    }

    @Transactional(readOnly = true)
    public List<DiplomaClaimRequestResponse> getPendingRequestsForUniversity(String universityUserEmail) {
        return claimRequestRepository
                .findAllByCertificate_University_User_EmailAndStatusOrderByCreatedAtAsc(
                        universityUserEmail,
                        DiplomaClaimRequestStatus.PENDING
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public DiplomaClaimRequestResponse reviewRequest(
            UUID requestId,
            ReviewDiplomaClaimRequest request,
            String universityUserEmail
    ) {
        DiplomaClaimRequest claimRequest = claimRequestRepository
                .findByIdAndCertificate_University_User_Email(requestId, universityUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("Claim request not found"));

        if (claimRequest.getStatus() != DiplomaClaimRequestStatus.PENDING) {
            throw new BusinessConflictException("Claim request already reviewed");
        }

        claimRequest.setReviewedAt(LocalDateTime.now());
        claimRequest.setReviewComment(request.reviewComment());

        if (request.approve()) {
            DiplomaCertificate certificate = claimRequest.getCertificate();

            if (certificate.getStudent() != null) {
                throw new BusinessConflictException("Diploma is already linked to a student");
            }

            certificate.setStudent(claimRequest.getStudent());
            claimRequest.setStatus(DiplomaClaimRequestStatus.APPROVED);
        } else {
            claimRequest.setStatus(DiplomaClaimRequestStatus.REJECTED);
        }

        return toResponse(claimRequest);
    }

    private DiplomaClaimRequestResponse toResponse(DiplomaClaimRequest entity) {
        return new DiplomaClaimRequestResponse(
                entity.getId(),
                entity.getCertificate().getId(),
                entity.getStudent().getId(),
                entity.getStatus().name(),
                entity.getCreatedAt(),
                entity.getReviewedAt(),
                entity.getReviewComment()
        );
    }
}