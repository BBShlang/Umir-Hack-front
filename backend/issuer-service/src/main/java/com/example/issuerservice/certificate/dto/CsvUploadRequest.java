package com.example.issuerservice.certificate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CsvUploadRequest {

    @Schema(type = "string", format = "binary", description = "CSV файл с дипломами")
    private MultipartFile file;
}