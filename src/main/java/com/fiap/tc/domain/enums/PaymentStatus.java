package com.fiap.tc.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PaymentStatus {
    WAITING("order payment pending", OrderStatus.PENDING),
    APPROVED("order payment approved", OrderStatus.PREPARING),
    REFUSED("payment refused order pending", OrderStatus.PENDING),
    ERROR("order payment error", OrderStatus.PENDING);

    private final String description;
    private final OrderStatus orderStatus;

    public static PaymentStatus from(String status) {
        for (PaymentStatus paymentStatus : PaymentStatus.values()) {
            if (paymentStatus.name().equals(status)) {
                return paymentStatus;
            }
        }
        throw new IllegalArgumentException(String.format("Invalid status: %s", status));
    }

}
