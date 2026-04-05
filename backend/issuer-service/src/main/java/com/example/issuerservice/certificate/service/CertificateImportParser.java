package com.example.issuerservice.certificate.service;

import com.example.issuerservice.certificate.dto.BulkImportRowError;
import com.example.issuerservice.certificate.dto.BulkIssueCertificateRow;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificateImportParser {

    public ParseResult parse(MultipartFile file) {
        List<BulkIssueCertificateRow> validItems = new ArrayList<>();
        List<BulkImportRowError> errors = new ArrayList<>();

        if (file == null || file.isEmpty()) {
            errors.add(new BulkImportRowError(0, "File is empty"));
            return new ParseResult(validItems, errors);
        }

        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            errors.add(new BulkImportRowError(0, "File name is missing"));
            return new ParseResult(validItems, errors);
        }

        String lowerName = filename.toLowerCase();

        try {
            if (lowerName.endsWith(".csv")) {
                parseCsv(file, validItems, errors);
            } else if (lowerName.endsWith(".xlsx")) {
                parseXlsx(file, validItems, errors);
            } else {
                errors.add(new BulkImportRowError(0, "Unsupported file format. Use .csv or .xlsx"));
            }
        } catch (Exception e) {
            errors.add(new BulkImportRowError(0, "Failed to read import file"));
        }

        return new ParseResult(validItems, errors);
    }

    private void parseCsv(
            MultipartFile file,
            List<BulkIssueCertificateRow> validItems,
            List<BulkImportRowError> errors
    ) throws Exception {
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
                    String universityCode = getRequired(record.get("universityCode"), "universityCode");
                    String diplomaNumber = getRequired(record.get("diplomaNumber"), "diplomaNumber");
                    String fullName = getRequired(record.get("fullName"), "fullName");
                    String specialty = getRequired(record.get("specialty"), "specialty");
                    Integer graduationYear = parseGraduationYear(record.get("graduationYear"));

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
        }
    }

    private void parseXlsx(
            MultipartFile file,
            List<BulkIssueCertificateRow> validItems,
            List<BulkImportRowError> errors
    ) throws Exception {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                errors.add(new BulkImportRowError(0, "Workbook does not contain sheets"));
                return;
            }

            Iterator<Row> rowIterator = sheet.iterator();
            if (!rowIterator.hasNext()) {
                errors.add(new BulkImportRowError(0, "Sheet is empty"));
                return;
            }

            Row headerRow = rowIterator.next();

            int universityCodeIndex = findColumnIndex(headerRow, "universityCode");
            int diplomaNumberIndex = findColumnIndex(headerRow, "diplomaNumber");
            int fullNameIndex = findColumnIndex(headerRow, "fullName");
            int specialtyIndex = findColumnIndex(headerRow, "specialty");
            int graduationYearIndex = findColumnIndex(headerRow, "graduationYear");

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                int rowNumber = row.getRowNum() + 1;

                if (isRowEmpty(row)) {
                    continue;
                }

                try {
                    String universityCode = getRequired(getCellString(row, universityCodeIndex), "universityCode");
                    String diplomaNumber = getRequired(getCellString(row, diplomaNumberIndex), "diplomaNumber");
                    String fullName = getRequired(getCellString(row, fullNameIndex), "fullName");
                    String specialty = getRequired(getCellString(row, specialtyIndex), "specialty");
                    Integer graduationYear = parseGraduationYear(getCellString(row, graduationYearIndex));

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
        }
    }

    private int findColumnIndex(Row headerRow, String expectedHeader) {
        for (Cell cell : headerRow) {
            String value = getCellString(cell).trim();
            if (expectedHeader.equalsIgnoreCase(value)) {
                return cell.getColumnIndex();
            }
        }
        throw new IllegalArgumentException("Column '" + expectedHeader + "' is required");
    }

    private String getCellString(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return getCellString(cell);
    }

    private String getCellString(Cell cell) {
        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                double numericValue = cell.getNumericCellValue();
                if (numericValue == Math.floor(numericValue)) {
                    yield String.valueOf((long) numericValue);
                }
                yield String.valueOf(numericValue);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue().trim();
                } catch (Exception e) {
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == Math.floor(numericValue)) {
                        yield String.valueOf((long) numericValue);
                    }
                    yield String.valueOf(numericValue);
                }
            }
            case BLANK -> "";
            default -> "";
        };
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }

        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            if (i < 0) {
                continue;
            }
            Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null && !getCellString(cell).isBlank()) {
                return false;
            }
        }
        return true;
    }

    private String getRequired(String value, String column) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Column '" + column + "' is required");
        }
        return value.trim();
    }

    private Integer parseGraduationYear(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Column 'graduationYear' is required");
        }

        try {
            return Integer.valueOf(raw.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid graduationYear");
        }
    }

    public record ParseResult(
            List<BulkIssueCertificateRow> validItems,
            List<BulkImportRowError> errors
    ) {
    }
}