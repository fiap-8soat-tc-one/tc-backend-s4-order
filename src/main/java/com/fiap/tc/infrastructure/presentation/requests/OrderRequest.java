package com.fiap.tc.infrastructure.presentation.requests;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class OrderRequest {
    
    @NotNull
    @NotEmpty
    @Valid
    @ApiModelProperty(
            value = "Order items request",
            required = true,
            dataType = "List<OrderItemRequest>"
    )
    private List<OrderItemRequest> orderItems;

}
