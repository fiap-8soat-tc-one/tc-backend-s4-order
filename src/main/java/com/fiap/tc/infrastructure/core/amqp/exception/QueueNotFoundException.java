package com.fiap.tc.infrastructure.core.amqp.exception;

public class QueueNotFoundException extends RuntimeException {
    public QueueNotFoundException(String s) {
        super(s);
    }
}