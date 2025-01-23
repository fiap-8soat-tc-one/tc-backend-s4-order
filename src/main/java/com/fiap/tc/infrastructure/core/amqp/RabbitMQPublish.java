package com.fiap.tc.infrastructure.core.amqp;

import com.fiap.tc.infrastructure.core.amqp.mapping.RabbitMQConfigurationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQPublish {

    private final RabbitTemplate rabbitTemplate;

    public void enqueue(final MessageContext context) {
        final RabbitMQConfigurationProperties.Binding binding = context.getRabbitMQBiding();
        final Object data = context.getRabbitMessage();
        final String exchange = binding.getTopic();
        log.info("Queuing {} to {}", data, exchange);

        rabbitTemplate.convertAndSend(exchange, binding.getRoutingKey(), data);
    }

    private MessageProperties getProperties(final MessageContext context) {
        return MessagePropertiesBuilder.fromProperties(context.getProperties()).build();
    }

}
