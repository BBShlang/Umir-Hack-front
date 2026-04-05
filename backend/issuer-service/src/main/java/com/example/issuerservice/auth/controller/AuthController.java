package com.example.issuerservice.auth.controller;

import com.example.issuerservice.auth.dto.*;
import com.example.issuerservice.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(
        name = "Аутентификация",
        description = "Регистрация и вход для университетов, студентов и работодателей"
)
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Регистрация университета",
            description = """
                    Создаёт пользователя с ролью UNIVERSITY,
                    генерирует криптографическую пару ключей
                    и сохраняет профиль университета
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Данные для регистрации университета",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegisterUniversityRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "universityName": "Astana IT University",
                                              "universityCode": "AITU-01",
                                              "email": "university@test.com",
                                              "password": "password123"
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Университет успешно зарегистрирован",
                            content = @Content(schema = @Schema(implementation = RegisterUniversityResponse.class))
                    ),
                    @ApiResponse(responseCode = "409", description = "Email или код университета уже существует")
            }
    )
    @PostMapping("/register/university")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterUniversityResponse registerUniversity(
            @Valid @RequestBody RegisterUniversityRequest request
    ) {
        return authService.registerUniversity(request);
    }

    @Operation(
            summary = "Регистрация студента",
            description = "Создаёт пользователя с ролью STUDENT и профиль студента",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Данные для регистрации студента",
                    content = @Content(
                            schema = @Schema(implementation = RegisterStudentRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                          "fullName": "Mary Johnson",
                                          "birthDate": "2004-05-10",
                                          "email": "student@test.com",
                                          "password": "password123"
                                        }
                                        """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Студент зарегистрирован"),
                    @ApiResponse(responseCode = "409", description = "Email уже существует")
            }
    )
    @PostMapping("/register/student")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterStudentResponse registerStudent(
            @Valid @RequestBody RegisterStudentRequest request
    ) {
        return authService.registerStudent(request);
    }

    @Operation(
            summary = "Регистрация работодателя",
            description = "Создаёт пользователя с ролью EMPLOYER и профиль компании",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Данные для регистрации работодателя",
                    content = @Content(
                            schema = @Schema(implementation = RegisterEmployerRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "companyName": "Tech Corp",
                                              "email": "employer@test.com",
                                              "password": "password123"
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Работодатель зарегистрирован"),
                    @ApiResponse(responseCode = "409", description = "Email уже существует")
            }
    )
    @PostMapping("/register/employer")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterEmployerResponse registerEmployer(
            @Valid @RequestBody RegisterEmployerRequest request
    ) {
        return authService.registerEmployer(request);
    }

    @Operation(
            summary = "Вход (логин)",
            description = "Аутентифицирует пользователя и возвращает JWT токен",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Email и пароль пользователя",
                    content = @Content(
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "email": "university@test.com",
                                              "password": "password123"
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный вход",
                            content = @Content(schema = @Schema(implementation = AuthResponse.class))
                    ),
                    @ApiResponse(responseCode = "401", description = "Неверный email или пароль")
            }
    )
    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest request
    ) {
        return authService.login(request);
    }
}