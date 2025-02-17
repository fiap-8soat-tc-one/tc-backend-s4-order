package com.fiap.tc.infrastructure.services;

import com.fiap.tc.infrastructure.core.config.RestClientOAuthConfig;
import com.fiap.tc.infrastructure.dto.AuthDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthLoginClient {
    static final String RESOURCE = "/api/public/v1/oauth/token";

    private final WebClient webClientOAuth;
    private final RestClientOAuthConfig restClientConfig;

    public AuthDetail execute() {

        return this.webClientOAuth.method(HttpMethod.POST).uri(RESOURCE)
                .headers(header -> header.set("Authorization", format("Basic %s", restClientConfig.getClientId())))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(format("username=%s&password=%s&grant_type=password", restClientConfig.getUserName(),
                        restClientConfig.getPassword()))
                .retrieve()
                .bodyToMono(AuthDetail.class)
                .retry(restClientConfig.getRetry())
                .timeout(Duration.ofSeconds(restClientConfig.getTimeout()))
                .block();


    }
}
