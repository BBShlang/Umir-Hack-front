package com.example.issuerservice.student.controller;

import com.example.issuerservice.student.dto.StudentProfileResponse;
import com.example.issuerservice.student.service.StudentProfileService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    @GetMapping("/me")
    @Operation(summary = "Получить профиль текущего студента")
    public StudentProfileResponse me(Authentication authentication) {
        return studentProfileService.getCurrentStudentProfile(authentication.getName());
    }
}