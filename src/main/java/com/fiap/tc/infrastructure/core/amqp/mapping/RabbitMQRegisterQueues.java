package com.fiap.tc.infrastructure.core.amqp.mapping;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.util.HashMap;

@Configuration
@RequiredArgsConstructor
public class RabbitMQRegisterQueues {

    private static final String DLQ_KEY_PROPERTY = "x-dead-letter-routing-key";
    private static final String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
    private static final String DLQ = ".dlq";
    private static final String DLX = ".dlx";

    private final RabbitMQConfigurationProperties rabbitMQConfigurationProperties;

    private final RabbitAdmin rabbitAdmin;


    @PostConstruct
    public void setUpBeans() {
        rabbitMQConfigurationProperties.getBindings()
                .stream()
                .filter(RabbitMQConfigurationProperties.Binding::isDeclare)
                .forEach(binding -> {
                    final Queue queue = createDefaultQueue(binding);
                    final Queue dlq = createDefaultDLQ(binding);
                    final Exchange exchange = ExchangeBuilder.topicExchange(binding.getTopic()).durable(true).build();
                    final Exchange dlx = ExchangeBuilder.topicExchange(binding.getTopic() + DLX).durable(true).build();
                    final Binding rabbitBinding = BindingBuilder.bind(queue).to(exchange).with(binding.getRoutingKey()).and(new HashMap<>());
                    final Binding bindingDlx = BindingBuilder.bind(dlq).to(dlx).with(binding.getRoutingKey()).and(new HashMap<>());

                    rabbitAdmin.declareQueue(queue);
                    rabbitAdmin.declareQueue(dlq);
                    rabbitAdmin.declareExchange(exchange);
                    rabbitAdmin.declareExchange(dlx);
                    rabbitAdmin.declareBinding(rabbitBinding);
                    rabbitAdmin.declareBinding(bindingDlx);
                });
    }

    private Queue createDefaultQueue(final RabbitMQConfigurationProperties.Binding binding) {
        return QueueBuilder.durable(binding.getQueue())
                .withArgument(X_DEAD_LETTER_EXCHANGE, binding.getTopic() + DLX)
                .withArgument(DLQ_KEY_PROPERTY, binding.getRoutingKey())
                .build();
    }

    private Queue createDefaultDLQ(final RabbitMQConfigurationProperties.Binding binding) {
        return QueueBuilder.durable(binding.getQueue() + DLQ)
                .withArgument(X_DEAD_LETTER_EXCHANGE, binding.getTopic() + DLX)
                .withArgument(DLQ_KEY_PROPERTY, binding.getRoutingKey())
                .build();
    }

}
