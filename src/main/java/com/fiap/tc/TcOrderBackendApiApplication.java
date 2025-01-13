package com.fiap.tc;

import com.fiap.tc.infrastructure.core.security.property.OriginApiProperty;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
@EnableConfigurationProperties(OriginApiProperty.class)
public class TcOrderBackendApiApplication {

    public static void main(String[] args) {

        SpringApplication.run(TcOrderBackendApiApplication.class, args);
    }


}
