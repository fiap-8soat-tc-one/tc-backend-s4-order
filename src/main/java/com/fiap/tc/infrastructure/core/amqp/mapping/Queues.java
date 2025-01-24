package com.fiap.tc.infrastructure.core.amqp.mapping;

public final class Queues {
    private Queues() {
        //default constructor
    }

    public static final String ORDER_CREATED_QUEUE = "ha.tc-order-backend-api.order.event.order.created.queue";
    public static final String PAYMENT_STATUS_UPDATED_QUEUE = "ha.tc-payment-backend-api.payment.event.updated.queue";


}
