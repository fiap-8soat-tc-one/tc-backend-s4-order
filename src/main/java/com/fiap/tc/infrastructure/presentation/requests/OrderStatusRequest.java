package com.fiap.tc.infrastructure.presentation.requests;

import com.fiap.tc.domain.enums.OrderStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderStatusRequest {

    @NotNull
    @ApiModelProperty(
            value = "Order Id",
            example = "7ba2a960-2354-466f-8868-6ad713742407",
            dataType = "UUID",
            required = true
    )
    private UUID id;

    @NotNull
    @ApiModelProperty(
            value = "Order status",
            example = "READY",
            dataType = "String",
            allowableValues = "RECEIVED, PENDING, PREPARING, READY, FINISHED, CANCELED",
            required = true
    )
    private OrderStatus status;
}
