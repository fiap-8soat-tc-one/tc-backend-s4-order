package com.fiap.tc.infrastructure.dto;

import lombok.Data;

@Data
public class AuthDetail {
    private String accessToken;
    private String tokenType;
    private String profile;
    private Integer expiresIn;
}
