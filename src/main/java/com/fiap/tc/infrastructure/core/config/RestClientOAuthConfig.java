package com.fiap.tc.infrastructure.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.rest.client.oauth")
@Data
public class RestClientOAuthConfig {
    private String endpoint;
    private Integer timeout;
    private Integer retry;
    private String clientId;
    private String userName;
    private String password;
}
