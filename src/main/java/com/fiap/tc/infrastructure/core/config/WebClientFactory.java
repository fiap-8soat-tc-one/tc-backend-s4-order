package com.fiap.tc.infrastructure.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class WebClientFactory {

    private final String oauthBaseUrl;
    private final String backofficeBaseUrl;

    public WebClientFactory(@Value("${app.rest.client.oauth.endpoint}") String oauthBaseUrl,
                            @Value("${app.rest.client.backoffice.endpoint}") String backofficeBaseUrl) {

        this.oauthBaseUrl = oauthBaseUrl;
        this.backofficeBaseUrl = backofficeBaseUrl;
    }

    @Bean
    public WebClient webClientBackoffice(WebClient.Builder builder) {
        return builder
                .baseUrl(backofficeBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(logRequest())
                .filter(logResponse())
                .build();

    }

    @Bean
    public WebClient webClientOAuth(WebClient.Builder builder) {
        return builder
                .baseUrl(oauthBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(logRequest())
                .filter(logResponse())
                .build();

    }

    private static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            logStatus(response);
            logHeaders(response);

            return logBody(response);
        });
    }

    private static void logStatus(ClientResponse response) {
        HttpStatus status = response.statusCode();
        log.info("Returned status code {} ({})", status.value(), status.getReasonPhrase());
    }

    private static Mono<ClientResponse> logBody(ClientResponse response) {
        if (response.statusCode().is4xxClientError() || response.statusCode().is5xxServerError()) {
            return response.bodyToMono(String.class).flatMap(body -> {
                log.info("Body is {}", body);
                return Mono.just(response);
            });
        } else {
            return Mono.just(response);
        }
    }

    private static void logHeaders(ClientResponse response) {
        response.headers().asHttpHeaders().forEach((name, values) -> {
            values.forEach(value -> {

                log.info(name, value);
            });
        });
    }

    public static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            logMethodAndUrl(request);
            logHeaders(request);

            return Mono.just(request);
        });
    }

    private static void logHeaders(ClientRequest request) {
        request.headers().forEach((name, values) -> {
            values.forEach(value -> {
                logNameAndValuePair(name, value);
            });
        });
    }

    private static void logNameAndValuePair(String name, String value) {
        log.info("{}={}", name, value);
    }

    private static void logMethodAndUrl(ClientRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.method().name());
        sb.append(" to ");
        sb.append(request.url());

        log.info(sb.toString());
    }

}
