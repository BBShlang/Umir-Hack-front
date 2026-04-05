package com.example.issuerservice.certificate.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateDiplomaClaimRequest(

        @NotBlank(message = "University code is required")
        String universityCode,

        @NotBlank(message = "Diploma number is required")
        String diplomaNumber
) {
}