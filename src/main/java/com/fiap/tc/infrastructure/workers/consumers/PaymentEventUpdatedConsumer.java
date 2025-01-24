package com.fiap.tc.infrastructure.workers.consumers;

import com.fiap.tc.infrastructure.workers.dto.PaymentUpdatedMessage;
import com.fiap.tc.infrastructure.core.amqp.dto.EventMessage;
import com.fiap.tc.infrastructure.core.amqp.mapping.Queues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentEventUpdatedConsumer {
    @RabbitListener(queues = Queues.PAYMENT_STATUS_UPDATED_QUEUE)
    public void dequeue(final Message<EventMessage<PaymentUpdatedMessage>> orderPaymentMessageMessage) {
        var message =
                orderPaymentMessageMessage.getPayload().getContent().stream().findFirst().orElseThrow(() -> new RuntimeException("message without content"));
        log.info("payment updated payload: {}", message);
    }
}
