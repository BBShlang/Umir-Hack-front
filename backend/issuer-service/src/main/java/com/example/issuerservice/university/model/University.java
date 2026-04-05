package com.example.issuerservice.university.model;

import com.example.issuerservice.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "universities")
public class University {

    @Id
    private UUID id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String publicKey;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String encryptedPrivateKey;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}