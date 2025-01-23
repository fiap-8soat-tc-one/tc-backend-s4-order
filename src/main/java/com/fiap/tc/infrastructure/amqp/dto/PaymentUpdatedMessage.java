package com.fiap.tc.infrastructure.amqp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentUpdatedMessage implements Serializable {
    private String transactionNumber;
    private String status;
}
