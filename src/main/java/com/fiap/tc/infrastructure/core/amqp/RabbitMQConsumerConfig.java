package com.fiap.tc.infrastructure.core.amqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.tc.infrastructure.core.amqp.mapping.RabbitMQConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConsumerConfig {

    private final ObjectMapper objectMapper;
    private final ConnectionFactory connectionFactory;

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jackson2JsonMessageConverter());
        return template;
    }

    @Bean
    public RabbitAdmin rabbitAdmin() {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(final RabbitMQConfigurationProperties configurationProperties) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(configurationProperties.getConfig().getConsumers());
        factory.setDefaultRequeueRejected(false);
        factory.setMessageConverter(jackson2JsonMessageConverter());

        ExponentialBackOffPolicy policy = new ExponentialBackOffPolicy();
        policy.setInitialInterval(configurationProperties.getConfig().getInitialInterval());
        policy.setMaxInterval(configurationProperties.getConfig().getMaxInterval());
        policy.setMultiplier(configurationProperties.getConfig().getMultiplier());

        factory.setAdviceChain(
                org.springframework.amqp.rabbit.config.RetryInterceptorBuilder
                        .stateless()
                        .maxAttempts(configurationProperties.getConfig().getRetries())
                        .recoverer(new RejectAndDontRequeueRecoverer())
                        .backOffPolicy(policy)
                        .build());

        return factory;
    }
}
