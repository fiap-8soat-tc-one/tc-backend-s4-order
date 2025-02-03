package com.fiap.tc.infrastructure.services;

import com.fiap.tc.domain.entities.Customer;
import com.fiap.tc.infrastructure.core.config.RestClientBackofficeConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Optional;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class CustomerServiceClient {
    static final String RESOURCE = "/api/private/v1/customers/%s";
    private final WebClient webClientBackoffice;
    private final RestClientBackofficeConfig restClientConfig;
    private final AuthTokenManager authTokenManager;

    @Cacheable(value = "customer", key = "#document")
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
