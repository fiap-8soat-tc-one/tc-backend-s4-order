package com.fiap.tc.infrastructure.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.rest.client.backoffice")
@Data
public class RestClientBackofficeConfig {
    private String endpoint;
    private Integer timeout;
    private Integer retry;
}
