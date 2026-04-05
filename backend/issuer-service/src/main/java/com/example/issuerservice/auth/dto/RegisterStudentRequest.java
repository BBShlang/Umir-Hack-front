package com.example.issuerservice.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterStudentRequest(

        @NotBlank(message = "Full name is required")
        @Size(max = 255, message = "Full name must be at most 255 characters")
        String fullName,

        @NotNull(message = "Birth date is required")
        LocalDate birthDate,

        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        @Size(max = 255, message = "Email must be at most 255 characters")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
        String password
) {
}