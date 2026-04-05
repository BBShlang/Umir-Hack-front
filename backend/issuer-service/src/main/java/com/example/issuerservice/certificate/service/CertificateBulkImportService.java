package com.example.issuerservice.certificate.service;

import com.example.issuerservice.certificate.dto.BulkImportCommitResponse;
import com.example.issuerservice.certificate.dto.BulkImportPreviewResponse;
import com.example.issuerservice.certificate.dto.BulkImportRowError;
import com.example.issuerservice.certificate.dto.BulkIssueCertificateRow;
import com.example.issuerservice.certificate.dto.IssueCertificateRequest;
import com.example.issuerservice.certificate.repository.DiplomaCertificateRepository;
import com.example.issuerservice.university.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificateBulkImportService {

    private final CertificateImportParser certificateImportParser;
    private final DiplomaCertificateRepository certificateRepository;
    private final UniversityRepository universityRepository;
    private final DiplomaCertificateService diplomaCertificateService;

    @Transactional(readOnly = true)
    public BulkImportPreviewResponse preview(MultipartFile file) {
        CertificateImportParser.ParseResult parseResult = certificateImportParser.parse(file);

        List<BulkIssueCertificateRow> validItems = new ArrayList<>();
        List<BulkImportRowError> errors = new ArrayList<>(parseResult.errors());

        for (BulkIssueCertificateRow row : parseResult.validItems()) {
            String validationError = validateRow(row);
            if (validationError == null) {
                validItems.add(row);
            } else {
                errors.add(new BulkImportRowError(row.rowNumber(), validationError));
            }
        }

        return new BulkImportPreviewResponse(
                parseResult.validItems().size() + parseResult.errors().size(),
                validItems.size(),
                errors.size(),
                validItems,
                errors
        );
    }

    @Transactional
    public BulkImportCommitResponse commit(MultipartFile file) {
        CertificateImportParser.ParseResult parseResult = certificateImportParser.parse(file);

        List<String> importedDiplomaNumbers = new ArrayList<>();
        List<BulkImportRowError> errors = new ArrayList<>(parseResult.errors());

        for (BulkIssueCertificateRow row : parseResult.validItems()) {
            try {
                String validationError = validateRow(row);
                if (validationError != null) {
                    errors.add(new BulkImportRowError(row.rowNumber(), validationError));
                    continue;
                }

                diplomaCertificateService.issue(new IssueCertificateRequest(
                        row.universityCode(),
                        row.diplomaNumber(),
                        row.fullName(),
                        row.specialty(),
                        row.graduationYear()
                ));

                importedDiplomaNumbers.add(row.diplomaNumber());
            } catch (Exception e) {
                errors.add(new BulkImportRowError(row.rowNumber(), e.getMessage()));
            }
        }

        int totalRows = parseResult.validItems().size() + parseResult.errors().size();

        return new BulkImportCommitResponse(
                totalRows,
                importedDiplomaNumbers.size(),
                errors.size(),
                importedDiplomaNumbers,
                errors
        );
    }

    private String validateRow(BulkIssueCertificateRow row) {
        if (!universityRepository.existsByCode(row.universityCode())) {
            return "University not found";
        }

        boolean exists = certificateRepository.existsByUniversity_CodeAndDiplomaNumber(
                row.universityCode(),
                row.diplomaNumber()
        );

        if (exists) {
            return "Diploma with this number already exists for this university";
        }

        if (row.graduationYear() < 1900 || row.graduationYear() > 3000) {
            return "Invalid graduationYear";
        }

        return null;
    }
}