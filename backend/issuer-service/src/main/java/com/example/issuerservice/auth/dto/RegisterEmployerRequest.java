package com.example.issuerservice.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterEmployerRequest(

        @NotBlank
        String companyName,

        @Email
        @NotBlank
        String email,

        @NotBlank
        @Size(min = 6, max = 100)
        String password
) {
}