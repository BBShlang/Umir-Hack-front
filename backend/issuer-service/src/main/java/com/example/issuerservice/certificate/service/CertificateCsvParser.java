package com.example.issuerservice.certificate.service;

import com.example.issuerservice.certificate.dto.BulkIssueCertificateRow;
import com.example.issuerservice.certificate.dto.BulkImportRowError;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificateCsvParser {

    public ParseResult parse(MultipartFile file) {
        List<BulkIssueCertificateRow> validItems = new ArrayList<>();
        List<BulkImportRowError> errors = new ArrayList<>();

        if (file == null || file.isEmpty()) {
            errors.add(new BulkImportRowError(0, "File is empty"));
            return new ParseResult(validItems, errors);
        }

        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT
                     .builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setTrim(true)
                     .build()
                     .parse(reader)) {

            for (CSVRecord record : parser) {
                int rowNumber = (int) record.getRecordNumber() + 1;

                try {
                    String universityCode = getRequired(record, "universityCode");
                    String diplomaNumber = getRequired(record, "diplomaNumber");
                    String fullName = getRequired(record, "fullName");
                    String specialty = getRequired(record, "specialty");
                    String graduationYearRaw = getRequired(record, "graduationYear");

                    Integer graduationYear;
                    try {
                        graduationYear = Integer.valueOf(graduationYearRaw);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid graduationYear");
                    }

                    validItems.add(new BulkIssueCertificateRow(
                            rowNumber,
                            universityCode,
                            diplomaNumber,
                            fullName,
                            specialty,
                            graduationYear
                    ));
                } catch (Exception e) {
                    errors.add(new BulkImportRowError(rowNumber, e.getMessage()));
                }
            }

        } catch (Exception e) {
            errors.add(new BulkImportRowError(0, "Failed to read CSV file"));
        }

        return new ParseResult(validItems, errors);
    }

    private String getRequired(CSVRecord record, String column) {
        String value = record.get(column);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Column '" + column + "' is required");
        }
        return value.trim();
    }

    public record ParseResult(
            List<BulkIssueCertificateRow> validItems,
            List<BulkImportRowError> errors
    ) {
    }
}