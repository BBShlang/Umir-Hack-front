package com.example.issuerservice.crypto.service;

import com.example.issuerservice.university.model.University;
import com.example.issuerservice.university.repository.UniversityRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UniversityKeyLookupService {

    private final UniversityRepository universityRepository;

    public UniversityKeys getKeysByCode(String universityCode) {
        University university = universityRepository.findByCode(universityCode)
                .orElseThrow(() -> new EntityNotFoundException("University not found"));

        return new UniversityKeys(
                university.getPublicKey(),
                university.getEncryptedPrivateKey()
        );
    }

    public record UniversityKeys(
            String publicKey,
            String privateKey
    ) {
    }
}