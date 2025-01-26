package com.fiap.tc.infrastructure.core.amqp.exception;

public class AmqpListenerException extends RuntimeException {
    public AmqpListenerException(String message, Throwable cause){
        super(message, cause);
    }
}
