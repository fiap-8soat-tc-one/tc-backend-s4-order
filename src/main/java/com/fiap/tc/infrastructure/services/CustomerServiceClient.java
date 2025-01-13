package com.fiap.tc.infrastructure.services;

import com.fiap.tc.domain.entities.Customer;
import com.fiap.tc.infrastructure.core.config.RestClientBackofficeConfig;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Optional;

import static java.lang.String.format;

@Service
public class CustomerServiceClient {
    static final String RESOURCE = "/api/public/v1/customers/%s";
    private final WebClient webClientBackoffice;
    private final RestClientBackofficeConfig restClientConfig;
    private final AuthTokenManager authTokenManager;

    public CustomerServiceClient(WebClient webClientBackoffice,
                                 RestClientBackofficeConfig restClientConfig,
                                 AuthTokenManager authTokenManager) {
        this.webClientBackoffice = webClientBackoffice;
        this.restClientConfig = restClientConfig;
        this.authTokenManager = authTokenManager;
    }

    public Optional<Customer> load(String document) {
        return Optional.ofNullable(this.webClientBackoffice.method(HttpMethod.GET).uri(format(RESOURCE, document))
                .headers(header -> {
                    header.set("Authorization", format("Bearer %s", authTokenManager.getToken()));
                    header.set("Accept", MediaType.APPLICATION_JSON_VALUE);
                })
                .retrieve()
                .bodyToMono(Customer.class)
                .retry(restClientConfig.getRetry())
                .timeout(Duration.ofSeconds(restClientConfig.getTimeout()))
                .block());
    }
}
