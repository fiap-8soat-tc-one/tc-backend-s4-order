package com.fiap.tc.infrastructure.core.amqp;

import com.fiap.tc.infrastructure.core.amqp.exception.QueueNotFoundException;
import com.fiap.tc.infrastructure.core.amqp.mapping.RabbitMQConfigurationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDateTime;

import static java.lang.String.format;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventPublisher {


    private final RabbitMQPublish publish;
    private final RabbitMQConfigurationProperties properties;

    public <T extends Serializable> void execute(final T eventMessage, final String queueName) {
        RabbitMQConfigurationProperties.Binding binding = properties.getQueueByName(queueName)
                .orElseThrow(() -> new QueueNotFoundException(format("Unable to find the given queue: '%s'",
                        queueName)));

        log.info("enqueueing message: {} at: {} queue, at: {}", eventMessage, queueName, LocalDateTime.now());
        publish.enqueue(new MessageContext(eventMessage, binding));
    }
}