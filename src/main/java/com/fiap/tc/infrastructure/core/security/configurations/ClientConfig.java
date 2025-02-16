package com.fiap.tc.infrastructure.core.security.configurations;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.client")
@Data
public class ClientConfig {
    private String signKey;
}
