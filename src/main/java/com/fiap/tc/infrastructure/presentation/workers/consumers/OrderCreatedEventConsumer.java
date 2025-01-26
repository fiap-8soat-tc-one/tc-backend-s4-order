package com.fiap.tc.infrastructure.presentation.workers.consumers;

import com.fiap.tc.infrastructure.presentation.workers.dto.OrderCreatedMessage;
import com.fiap.tc.infrastructure.core.amqp.dto.EventMessage;
import com.fiap.tc.infrastructure.core.amqp.mapping.Queues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderCreatedEventConsumer {
    @RabbitListener(queues = Queues.ORDER_CREATED_QUEUE)
    public void dequeue(final Message<EventMessage<OrderCreatedMessage>> orderCreatedMessageMessage) {
        var message =
                orderCreatedMessageMessage.getPayload().getContent().stream().findFirst().orElseThrow(() -> new RuntimeException("message without content"));
        log.info("order created payload: {}", message);

    }
}
