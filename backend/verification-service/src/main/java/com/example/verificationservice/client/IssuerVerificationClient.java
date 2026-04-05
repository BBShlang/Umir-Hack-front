package com.example.verificationservice.client;

import com.example.verificationservice.client.dto.IssuerVerificationBundleResponse;
import com.example.verificationservice.verification.dto.VerifyByQrRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class IssuerVerificationClient {

    private final RestClient restClient;

    public IssuerVerificationClient(
            @Value("${app.issuer.base-url}") String issuerBaseUrl
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(issuerBaseUrl)
                .build();
    }

    public IssuerVerificationBundleResponse resolveVerificationBundle(VerifyByQrRequest request) {
        return restClient.post()
                .uri("/internal/api/v1/verifications/resolve")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(IssuerVerificationBundleResponse.class);
    }
}