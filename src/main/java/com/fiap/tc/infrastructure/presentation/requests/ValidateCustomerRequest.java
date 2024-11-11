package com.fiap.tc.infrastructure.presentation.requests;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ValidateCustomerRequest {

    @NotEmpty
    @ApiModelProperty(
            value = "JWT Token",
            required = true,
            dataType = "String"
    )
    private String accessToken;
}
