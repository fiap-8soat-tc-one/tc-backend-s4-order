package com.fiap.tc.infrastructure.services;

import com.fiap.tc.domain.entities.Product;
import com.fiap.tc.infrastructure.core.config.RestClientBackofficeConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class ProductServiceClient {
    static final String RESOURCE = "/api/private/v1/products/%s";
    private final WebClient webClientBackoffice;
    private final RestClientBackofficeConfig restClientConfig;
    private final AuthTokenManager authTokenManager;


    @Cacheable(value = "product", key = "#id")
    public Optional<Product> load(UUID id) {
        return Optional.ofNullable(this.webClientBackoffice.method(HttpMethod.GET).uri(format(RESOURCE, id.toString()))
                .headers(header -> {
                    header.set("Authorization", format("Bearer %s", authTokenManager.getToken()));
                    header.set("Accept", MediaType.APPLICATION_JSON_VALUE);
                })
                .retrieve()
                .bodyToMono(Product.class)
                .retry(restClientConfig.getRetry())
                .timeout(Duration.ofSeconds(restClientConfig.getTimeout()))
                .block());
    }
}
