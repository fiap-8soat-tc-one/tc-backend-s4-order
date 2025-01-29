package com.fiap.tc.infrastructure.core.amqp;

import com.fiap.tc.infrastructure.core.amqp.exception.QueueNotFoundException;
import com.fiap.tc.infrastructure.core.amqp.mapping.RabbitMQConfigurationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

import static java.lang.String.format;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventPublisher {

    public static final String TRACKING_ID = "tracking_id";
    private final RabbitMQPublish publish;
    private final RabbitMQConfigurationProperties properties;

    public <T extends Serializable> void execute(final T eventMessage, final String queueName,
                                                 final String trackingId) {
        RabbitMQConfigurationProperties.Binding binding = properties.getQueueByName(queueName)
                .orElseThrow(() -> new QueueNotFoundException(format("Unable to find the given queue: '%s'",
                        queueName)));

        log.info("enqueueing tracking-id {} message: {} at: {} queue, at: {}", trackingId, eventMessage, queueName,
                LocalDateTime.now());


        publish.enqueue(new MessageContext(eventMessage, binding, Map.of(TRACKING_ID, trackingId)));
    }
}